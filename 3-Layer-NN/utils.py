def extract_label(image_file):
    char_to_label_mapping = {"2" : 0, "4" : 1, "5" : 2, "7" : 3, "8" : 4, "a" : 5, "d" : 6, "e" : 7, 
                             "f": 8, "i" : 9, "j" : 10, "p" : 11, "s" : 12, "t" : 13, "v" : 14}
    file_label = image_file[0].lower()
    label = char_to_label_mapping.get(file_label)
        
    return label

def label_to_char(label):
    label_array = ["2", "4", "5", "7", "8", "a", "d", "e", 
                    "f", "i", "j", "p", "s", "t", "v"]
    return label_array[label]



