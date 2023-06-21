from PIL import Image
import math
from transformers import AutoModelForImageClassification, AutoFeatureExtractor
import torch

class ViTForUAVHuman():
    def __init__(self) -> None:
        self.model = AutoModelForImageClassification.from_pretrained("Heartsream/vit-KAIYI", use_auth_token='REQUEST FOR IT') ##request the token if you want to use it 
        self.feature_extractor = AutoFeatureExtractor.from_pretrained("google/vit-base-patch16-224-in21k") ##same feature extractor that was used for feature extraction during training

    def __repr__(self):
        return "Vision Transformer"
    
    def convert_label_to_str(self,label):
        label_dict = {0 : "No Gender", 1 : "Male", 2 : "Female", 
                    3 : "No Backpack", 4 : "Red Backpack", 5 : "Black Backpack", 6 : "Green Backpack", 7 : "Yellow Backpack", 8 : "No Backpack", 
                    9 : "No Hat", 10 : "Red Hat", 11 : "Black Hat", 12 : "Yellow Hat", 13 : "White Hat", 14 : "No Hat", 
                    15 : "Upper Clothing None", 16 : "Upper Clothing Red", 17 : "Upper Clothing Black", 18 : "Upper Clothing Blue", 19 : "Upper Clothing Green", 20 : "Upper Clothing Multicolor", 21 : "Upper Clothing Grey", 22 : "Upper Clothing White", 23 : "Upper Clothing Yellow", 24 : "Upper Clothing DarkBrown", 25 : "Upper Clothing Purple", 26 : "Upper Clothing Pink", 
                    27 : "Upper Style None", 28 : "Upper Style Long", 29 : "Upper Style Short", 30 : "Upper Style Skirt", 
                    31 : "Lower Clothing None", 32 : "Lower Clothing Red", 33 : "Lower Clothing Black", 34 : "Lower Clothing Blue", 35 : "Lower Clothing Green", 36 : "Lower Clothing Multicolor", 37 : "Lower Clothing Grey", 38 : "Lower Clothing White", 39 : "Lower Clothing Yellow", 40 : "Lower Clothing DarkBrown", 41 : "Lower Clothing Purple", 42 : "Lower Clothing Pink", 
                    43 : "Lower Style None", 44 : "Lower Style Long", 45 : "Lower Style Short", 46 : "Lower Style Skirt"}
    
        return label_dict.get(label)

    def predict(self, arr, start_index, end_index):
        max_value = -math.inf
        max_index = None
        total = 0
        for i in range(start_index, end_index):
            total += arr[i]
            if arr[i] > max_value:
                max_value = arr[i]
                max_index = i
            
        return max_index, float(arr[max_index] / total)

    def model_inference(self,image_filename : str):
        image = Image.open(image_filename).convert("RGB")
        
        # prepare image for the model
        encoding = self.feature_extractor(image, return_tensors="pt")
        
        # forward pass
        with torch.no_grad():
            outputs = self.model(**encoding)
            logits = outputs.logits
        
        ##apply softmax to get the probability of each label
        probs = torch.nn.functional.softmax(logits, dim = 1)
        offset_array = [3,6,6,12,4,12,4] ##our dataset has 7 labels
        label_category = ["Gender", "Backpack", "Hat", "Upper Clothing Colour", "Upper Clothing Style", "Lower Clothing Colour", "Lower Clothing Style"]
        y_confidence = [] ##to store the conficence level for each prediction
        result = [] ##to store the predicted attributes

        offset = 0
        for i in range(len(offset_array)):
            selected_label, confidence = self.predict(probs[0], offset, offset_array[i]+offset)

            ##add the formated string of "category" : "result"
            result.append(" : ".join([label_category[i],self.convert_label_to_str(selected_label)]))
            y_confidence.append(confidence)
            offset += offset_array[i]
        
        return y_confidence, result