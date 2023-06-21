import os
import json
from utils import *
from feature_extractor import feature_extractor
import numpy as np
from neural_net import NeuralNet
import sys

def test_data(model, test_file):
    correct_prediction = 0
    total_images = 0
    correct_images = []
    ##process the test file
    for image_file in os.listdir(test_file):
        total_images += 1 ##count the number of test files
        relative_filename = os.path.join(test_file, image_file)
        prediction, confidence = model.predict(feature_extractor(relative_filename))
        if prediction == extract_label(image_file):
            correct_prediction += 1
            correct_images.append((image_file, confidence))
    return (correct_prediction / total_images), correct_images ##accuracy = correct classification / all classification (for multiclass labelling)

def load_model(json_file):
    with open(json_file, "r") as model_file:
        model_parameter = json.loads(model_file.read())
        input_neurons = model_parameter["input_neurons"] 
        hidden_neurons = model_parameter["hidden_neurons"] 
        output_neurons = model_parameter["output_neurons"] 
        learning_rate = model_parameter["learning_rate"]
        input_hidden_weight = np.asarray(model_parameter["input_hidden_weight"])
        hidden_output_weight = np.asarray(model_parameter["hidden_output_weight"])
        hidden_bias = np.asarray(model_parameter["hidden_bias"])
        output_bias = np.asarray(model_parameter["output_bias"])
        model = NeuralNet(input_neurons, hidden_neurons, output_neurons,learning_rate)
        model.load_weight_bias(input_hidden_weight, hidden_output_weight, hidden_bias, output_bias)

    return model

def model_inference(model, image):
    prediction = model.predict(feature_extractor(image))[0] ##don't need the confidence here
    return label_to_char(prediction)
    

if __name__ == "__main__":
    model_name = "char_recognition"
    model = load_model(f"{model_name}.json")
    image_filename = sys.argv[1]
    print(model_inference(model,image_filename))

    ############# testing the model #############
    #print(test_data(load_model(f"{model_name}.json"), "Dataset/Test"))