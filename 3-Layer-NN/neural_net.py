import numpy as np
import math
import json
import os
from utils import extract_label
from feature_extractor import feature_extractor

seed = 7 ##to allow replication of weights
np.random.seed(seed) ##setting up seed

class NeuralNet():
    def __init__(self, input_neurons, hidden_neurons, output_neurons, learning_rate):
        self.image_inputs = None
        self.image_targets = None
        self.learning_rate = learning_rate
        self.max_iteration = 1000
        self.iteration = 0

        ##weight and bias initialization
        self.input_hidden_weight = np.random.uniform(-0.5,0.5,size =(hidden_neurons, input_neurons))
        self.hidden_output_weight = np.random.uniform(-0.5,0.5,size =(output_neurons, hidden_neurons))
        self.hidden_bias = np.random.uniform(0, 1, size=(hidden_neurons, 1))
        self.output_bias = np.random.uniform(0, 1, size=(output_neurons, 1))
    
    def update_training_data(self, image_inputs, image_targets):
        self.image_inputs = image_inputs
        self.image_targets = image_targets
        self.iteration = 0

    def load_model(self, input_hidden_weight, hidden_output_weight, hidden_bias, output_bias):
        self.input_hidden_weight = input_hidden_weight
        self.hidden_output_weight = hidden_output_weight
        self.hidden_bias = hidden_bias
        self.output_bias = output_bias

    def forward_input_hidden(self, image_input):
        hidden_layer_result =  np.zeros(len(self.input_hidden_weight))
        for index, input_weight in enumerate(self.input_hidden_weight):
            hidden_layer_result[index] = self.forward(image_input, input_weight, self.hidden_bias[index][0])
        
        return hidden_layer_result

    def forward_hidden_output(self, hidden_layer_result):
        output_layer_result = np.zeros(len(self.hidden_output_weight))
        for index, hidden_weight in enumerate(self.hidden_output_weight):
            output_layer_result[index] = self.forward(hidden_layer_result, hidden_weight, self.output_bias[index][0])
        
        return output_layer_result
        
    def forward(self, inputs, weights, bias):
        return self.sigmoid(np.dot(inputs,weights) + bias)

    def RELU(self, inputs):
        return max(0,inputs)
    
    def sigmoid(self, inputs):
        return 1/(1+np.exp(-inputs))
    
    def forward_nn(self, image_input):
        hidden_layer_result = self.forward_input_hidden(image_input)
        output_layer_result = self.forward_hidden_output(hidden_layer_result)
        return hidden_layer_result, output_layer_result

    def check_for_end(self, neuron_outputs):
        to_end = True
        
        total_error = 0

        ##for each item in the batch, find the error
        for i in range(len(self.image_targets)):
            ##calculate error based on the image target
            for j, output in enumerate(neuron_outputs[i][1]):
                error = 0.5*(output - (0 if j != self.image_targets[i] else 1))**2
                total_error += error
        
        average_error = total_error / len(neuron_outputs)
        if average_error > 0.05 and self.iteration < self.max_iteration:
            to_end = False
        

        print(f"Average error after {self.iteration} backward propagation: {average_error}")
        self.iteration += 1
        
        return to_end
    
    def weight_bias_correction_output(self, hidden_layer_result, output_layer_result, image_target):
        ##output should be 2d matrix, where each row represent the weight for each output neuron
        weight_changes = np.zeros(self.hidden_output_weight.shape)
        bias_changes = np.zeros(self.output_bias.shape)
        ##go through the weight from hidden node to output node
        for row in range(self.hidden_output_weight.shape[0]): ##updating the incoming weights for each output node
            output_k = output_layer_result[row]
            target = (0 if row != image_target else 1)
            delta_k = (output_k - target) * (output_k * (1-output_k))
            bias_changes[row] = delta_k
            for col in range(self.hidden_output_weight.shape[1]):
                output_j = hidden_layer_result[col]
                weight_changes[row][col] = delta_k * output_j

        return weight_changes, bias_changes

    def weight_bias_correction_hidden(self, hidden_layer_result, output_bias_changes, image_input):
        ##output should be 2d matrix, where each row represent the weight for each output neuron
        weight_changes = np.zeros(self.input_hidden_weight.shape)
        bias_changes = np.zeros(self.hidden_bias.shape)

        ##go through the weight from input to hidden node
        for row in range(self.input_hidden_weight.shape[0]):
            output_j = hidden_layer_result[row]
            delta_j = (output_j * (1-output_j)) * sum(self.hidden_output_weight[i][row] * output_bias_changes[i] for i in range(self.hidden_output_weight.shape[0]))
            bias_changes[row] = delta_j
            for col in range(self.input_hidden_weight.shape[1]):
                weight_changes[row][col] = image_input[col] * delta_j


        return weight_changes, bias_changes
    
    def weight_bias_update(self, neuron_outputs):
        aggregated_hidden_weight_changes = np.zeros(self.hidden_output_weight.shape)
        aggregated_output_bias_changes = np.zeros(self.output_bias.shape)
        aggregated_input_weight_changes = np.zeros(self.input_hidden_weight.shape)
        aggregated_hidden_bias_changes = np.zeros(self.hidden_bias.shape)
        
        ##go through each hidden layer and output layer result for each item in the batch
        for i in range(len(neuron_outputs)):
            hidden_weight_changes, output_bias_changes = self.weight_bias_correction_output(neuron_outputs[i][0], neuron_outputs[i][1], self.image_targets[i])
            input_weight_changes, hidden_bias_changes = self.weight_bias_correction_hidden(neuron_outputs[i][0],output_bias_changes, self.image_inputs[i])
            aggregated_hidden_weight_changes += hidden_weight_changes
            aggregated_output_bias_changes += output_bias_changes
            aggregated_input_weight_changes += input_weight_changes
            aggregated_hidden_bias_changes += hidden_bias_changes
        
        ##find the average weight and bias changes
        aggregated_hidden_weight_changes /= len(neuron_outputs)
        aggregated_output_bias_changes /= len(neuron_outputs)
        aggregated_input_weight_changes /= len(neuron_outputs)
        aggregated_hidden_bias_changes /= len(neuron_outputs)
            

        ##update weight and bias from hidden layer to output layer
        for row in range(self.hidden_output_weight.shape[0]):
            self.output_bias[row] = self.output_bias[row] - (self.learning_rate*aggregated_output_bias_changes[row])
            for col in range(self.hidden_output_weight.shape[1]):
                self.hidden_output_weight[row][col] = self.hidden_output_weight[row][col] - (self.learning_rate*aggregated_hidden_weight_changes[row][col])
        
        ##update weight and bias from input layer to hidden layer
        for row in range(self.input_hidden_weight.shape[0]):
            self.hidden_bias[row] = self.hidden_bias[row] - (self.learning_rate*aggregated_hidden_bias_changes[row])
            for col in range(self.input_hidden_weight.shape[1]):
                self.input_hidden_weight[row][col] = self.input_hidden_weight[row][col] - (self.learning_rate*aggregated_input_weight_changes[row][col])


    def saving_weights_bias(self, filename):
        model_parameter = {}
        model_parameter["input_neurons"] = self.input_hidden_weight.shape[1]
        model_parameter["hidden_neurons"] = self.input_hidden_weight.shape[0]
        model_parameter["output_neurons"] = self.hidden_output_weight.shape[0]
        model_parameter["learning_rate"] = self.learning_rate
        model_parameter["input_hidden_weight"] = self.input_hidden_weight.tolist()
        model_parameter["hidden_output_weight"] = self.hidden_output_weight.tolist()
        model_parameter["hidden_bias"] = self.hidden_bias.tolist()
        model_parameter["output_bias"] = self.output_bias.tolist()
        
        with open(filename, "w") as output:
            output.write(json.dumps(model_parameter, indent= 4))
        
    def load_weight_bias(self, input_hidden_weight, hidden_output_weight, hidden_bias, output_bias):
        self.input_hidden_weight = input_hidden_weight
        self.hidden_output_weight = hidden_output_weight
        self.hidden_bias = hidden_bias
        self.output_bias = output_bias
        

    def train(self):
        done_training = False
        while not done_training:
            neuron_outputs = []
            for index in range(len(self.image_inputs)):
                ##train on the batch data
                neuron_outputs.append(self.forward_nn(self.image_inputs[index]))
            if not self.check_for_end(neuron_outputs):
                self.weight_bias_update(neuron_outputs)
            else:
                done_training = True
    
    def softmax(self,x):
        return(np.exp(x - np.max(x)) / np.exp(x - np.max(x)).sum())

    def predict(self,image_input):
        logits = self.forward_nn(image_input)[1]
        result = np.argmax(self.softmax(logits))
        return result, logits[result]  ##to get the confidence




def data_processing(training_file):
    """
    This function used to prepare training dataset, where we extract features of the images and mark them with their labels
    I also chose to seperate the training dataset into batches
    """
    data_batches = [None] * 4 ##train with 4 data batches
    for i in range(len(data_batches)):
        data_batches[i] = ([],[])

    ##loop through the training file
    for image_file in os.listdir(training_file):
        relative_filename = os.path.join(training_file, image_file)
        file_ord_value = ord(image_file[0])
        if file_ord_value >= 50 and file_ord_value <= 56:
            bin_to_insert = data_batches[(ord(image_file[1]) - 97)//2]
        else:
            bin_to_insert = data_batches[(((int(image_file[1]) + 1)// 2) - 1)] ##process each image into their respective data batch
        bin_to_insert[0].append(feature_extractor(relative_filename))
        bin_to_insert[1].append(extract_label(image_file))
    
    return data_batches


if __name__ == "__main__":
    ############ training the model ############
    training_file = "Dataset/Train"
    model_name = "char_recognition"
    data_batches = data_processing(training_file)
    input_nodes = len(data_batches[0][0][0]) ##get the size of flatten array for the first image
    output_nodes = 15
    hidden_nodes = int((math.sqrt(input_nodes * output_nodes) // 10) * 10)
    print(f"Commence training\nInput nodes: {input_nodes}, Hidden Nodes : {hidden_nodes}")
    network = NeuralNet(input_nodes, hidden_nodes, output_nodes, 0.5)
    
    #train the network 
    count_batch = 1
    for data_batch in data_batches:
        print(f"\nProcessing Data Batch {count_batch}\n")
        count_batch += 1
        training_input = data_batch[0]
        training_target = data_batch[1]
        network.update_training_data(training_input, training_target)
        network.train()
    
    network.saving_weights_bias(f"{model_name}.json")

    
    