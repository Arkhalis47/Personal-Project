import sys

def read_binary_file(bin_file_name):
    input_byte_array = bytearray()

    binfile = open(bin_file_name,'rb')
    byte_reading = binfile.read(1)
    while byte_reading:
        input_byte_array += byte_reading
        byte_reading = binfile.read(1)

    binfile.close()

    return input_byte_array

def write_decoded(filename_array, output_array):
    filename = ""
    for char in filename_array:
        filename = "".join([filename, char])

    output_file =  open(filename, 'w')
    for char in output_array:
        output_file.write(char)
    output_file.close()


def decode_bin(bin_file_name):
    input_byte_array = read_binary_file(bin_file_name)
    formatted_byte = format_byte(input_byte_array)
    byte_position = 1 #the first byte is the one i am now processing
    bit_position = 0
    formatted_bit = formatted_byte[0]

    #length of the input filename(elias)
    formatted_bit, bit_position, byte_position, len_input_filename = decode_elias(formatted_bit, bit_position, byte_position,formatted_byte)


    #decoding the filename(ASCII in 8 bits)
    filename_array = [None] * len_input_filename
    for i in range(len_input_filename):
        formatted_bit, bit_position, byte_position, ascii_char = decode_ascii(formatted_bit, bit_position, byte_position, formatted_byte)
        filename_array[i] = chr(ascii_char)

    #Total number of character in the file (elias)
    formatted_bit, bit_position, byte_position, number_of_char = decode_elias(formatted_bit, bit_position, byte_position,formatted_byte)

    #Total number of distinct character in the file (elias)
    formatted_bit, bit_position, byte_position, number_of_uniq_char = decode_elias(formatted_bit, bit_position, byte_position,formatted_byte)

    #Decoding of all the distinct character, store the huffman encoding into binary search tree
    huffman_tree = binary_search_tree()
    for i in range(number_of_uniq_char):
        #decode ascii char
        formatted_bit, bit_position, byte_position, ascii_char = decode_ascii(formatted_bit, bit_position, byte_position, formatted_byte)

        #decode the length of the encoding(elias)
        formatted_bit, bit_position, byte_position, length_of_encode = decode_elias(formatted_bit, bit_position, byte_position,formatted_byte)

        #read the huffman encoding
        formatted_bit, bit_position, byte_position, huffman_encode = read_n_bits(formatted_bit, bit_position, byte_position, formatted_byte,length_of_encode)
        
        #store the huffman_encode
        huffman_tree.add_into_tree(huffman_encode,ascii_char)


    #start of lz77
    output_string = []
    number_of_char_decoded = 0
    while number_of_char_decoded < number_of_char:
        #elias, number to go back
        formatted_bit, bit_position, byte_position, number_to_go_back = decode_elias(formatted_bit, bit_position, byte_position,formatted_byte)

        #elias, number to copy
        formatted_bit, bit_position, byte_position, number_to_copy = decode_elias(formatted_bit, bit_position, byte_position,formatted_byte)

        #huffman, what is the next character
        found_char = False
        node_now = huffman_tree.root
        while not found_char:
            formatted_bit, bit_position, byte_position, tree_position = read_n_bits(formatted_bit, bit_position, byte_position, formatted_byte,1)
            if tree_position[0] == 1: #go right
                node_now = node_now.right
            elif tree_position[1] == 1: # go left
                node_now = node_now.left
            
            if node_now.ascii_char != None:
                found_char = True
        
        next_character = node_now.ascii_char

        offset_index = number_of_char_decoded - number_to_go_back

        for i in range(number_to_copy):
            output_string.append(output_string[offset_index])
            number_of_char_decoded += 1
            offset_index += 1

        output_string.append(next_character)
        number_of_char_decoded += 1
    
    write_decoded(filename_array, output_string)
    


def decode_ascii(formatted_bit, bit_position, byte_position, formatted_byte):
    #read 8 bits because its ascii character
    formatted_bit, bit_position, byte_position, result = read_n_bits(formatted_bit, bit_position, byte_position, formatted_byte,8)
    return formatted_bit, bit_position, byte_position, result[0]

def decode_elias(formatted_bit, bit_position, byte_position, formatted_byte):
    #read the first bit
    formatted_bit, bit_position, byte_position, result = read_n_bits(formatted_bit, bit_position, byte_position, formatted_byte,1)
    value_decoded = None

    #if the first bit is 1
    if result[0] == 1:
        value_decoded = 0
    
    #it denotes the length, proceed to decode
    else:
        while result[1] != 0:
            #do flipping of MSB here
            value_to_add = 2 ** (get_bit_length(result[0])+ result[1] - 1)
            value_of_binary = result[0] + value_to_add + 1 #elias encoding for length is always length - 1
            formatted_bit, bit_position, byte_position, result = read_n_bits(formatted_bit, bit_position, byte_position, formatted_byte,value_of_binary)
        value_decoded = result[0] - 1 #since the elias encoding start from 0
    
    return formatted_bit, bit_position, byte_position, value_decoded


    

def read_n_bits(formatted_bit, bit_position, byte_position, formatted_byte ,number_to_read):
    #Whether i added a new byte
    added_new_byte = False

    #check the total length of the formatted_bit
    total_length = get_bit_length(formatted_bit[0]) + formatted_bit[1]

    #if the last bit to read is out of bound, then concatenate with a new byte
    while (bit_position + number_to_read - 1) > (total_length - 1):
        if byte_position > (len(formatted_byte) - 1):
            raise Exception("No more bytes to read") #which should not happen
    
        next_formatted_bit = formatted_byte[byte_position]
        byte_position += 1
        formatted_bit = combine_bits(formatted_bit, next_formatted_bit)
        total_length = get_bit_length(formatted_bit[0]) + formatted_bit[1]
        added_new_byte = True


    result = [0,0]
    for i in range(number_to_read):
        bit_processed = read_bit(formatted_bit, bit_position)
        result = combine_bits(result, bit_processed) #concatenate the bits
        bit_position += 1



    if added_new_byte:
        total_length = get_bit_length(formatted_bit[0]) + formatted_bit[1]

        #i can take away bits that was read
        while total_length > 8:
            if bit_position < 8:
                raise Exception("First 8 bits was not processed yet")

            formatted_bit = take_away_8bit(formatted_bit) #take away the first 8 bits
            bit_position -= 8 #reset the position
            total_length = get_bit_length(formatted_bit[0]) + formatted_bit[1] #update the total length

    
    return formatted_bit, bit_position, byte_position, result

def take_away_8bit(bit):
    '''
    Make sure that input must be in this form 
    [value, number of zeros padded to the left]
    '''
    number = bit[0]
    total_bits = bit[1] + get_bit_length(number)

    #remove the remaining bits that we want to continue with
    temp = number >> (total_bits - 8)

    #prepare for XOR operation
    temp = temp << (total_bits - 8)

    #take away the first 8 bits with XOR operation
    number = number ^ temp
    bits_remaining = total_bits - 8
    number_of_0_padded = bits_remaining - get_bit_length(number)
    return [number,number_of_0_padded]


def combine_bits(bit_1,bit_2):
    '''
    Make sure that input must be in this form 
    [value, number of zeros padded to the left]
    '''
    value_of_bit_1 = bit_1[0]
    value_of_bit_2 = bit_2[0]
    total_bits = get_bit_length(value_of_bit_1) + get_bit_length(value_of_bit_2) + bit_1[1] + bit_2[1]

    #pad to left(including 0s padded to left of bit 2), allowing for concatenation
    value_of_bit_1 = value_of_bit_1 << (get_bit_length(value_of_bit_2) + bit_2[1]) 
    value_of_bit_1 = value_of_bit_1 | value_of_bit_2 # concatenate accordingly

    number_of_0_padded = total_bits - get_bit_length(value_of_bit_1)
    return [value_of_bit_1,number_of_0_padded]


def read_bit(formatted_bit, bit_position):
    '''
    bit_position start from 0
    '''

    bit_length = get_bit_length(formatted_bit[0]) + formatted_bit[1]


    if bit_position > bit_length - 1:
        raise Exception("bit position is out of bound")

    number_to_compare = 2 ** (bit_length - bit_position - 1)

    answer =  number_to_compare & formatted_bit[0]

    if answer == 0:
        return [0,1]
    elif answer == number_to_compare:
        return [1,0]
    else:
        raise Exception("Something wrong when reading the bit")


def get_bit_length(number):
    length_of_bit = 0
    while number > 0:
        length_of_bit += 1
        number = number // 2
    return length_of_bit


def format_byte(input_byte_array):
    formatted_byte = []
    for byte in input_byte_array:
        number_of_0_padded = 8 - get_bit_length(byte)
        formatted_byte.append([byte, number_of_0_padded])
    return formatted_byte


class binary_node():
    def __init__(self):
        self.ascii_char = None
        self.left = None
        self.right = None 

class binary_search_tree():
    '''
    A standard binary search tree to store the huffman encoding
    '''
    def __init__(self):
        self.root = binary_node()
    
    def add_into_tree(self, huffman_encoding, ascii_char_rep):
        node_now = self.root
        total_bit_length = get_bit_length(huffman_encoding[0]) + huffman_encoding[1]
        position_array = []
        for i in range(total_bit_length):
            result = read_bit(huffman_encoding,i)
            if result[0] == 1:
                position_array.append(1)
            elif result[1] == 1:
                position_array.append(0)
        for i in position_array:
            if i == 1:
                if node_now.right == None:
                    node_now.right = binary_node()
                node_now = node_now.right
            elif i == 0:
                if node_now.left == None:
                    node_now.left = binary_node()
                node_now = node_now.left
            else:
                raise Exception("Unknown input")
        
        if node_now.ascii_char != None:
            raise Exception("Something is wrong with encoding")
        
        node_now.ascii_char = chr(ascii_char_rep)
        
    def search_encoding(self, huffman_encoding):
        node_now = self.root
        total_bit_length = get_bit_length(huffman_encoding[0]) + huffman_encoding[1]
        position_array = []
        for i in range(total_bit_length):
            result = read_bit(huffman_encoding,i)
            if result[0] == 1:
                position_array.append(1)
            elif result[1] == 1:
                position_array.append(0)

        for i in position_array:
            if i == 1:
                if node_now.right == None:
                    raise Exception("Right node is none")
                node_now = node_now.right
            elif i == 0:
                if node_now.left == None:
                    raise Exception("Left node is none")
                node_now = node_now.left
            else:
                raise Exception("Unknown input")
        
        if node_now.ascii_char == None:
            raise Exception("Something is wrong with encoding")
        
        return node_now.ascii_char

        
if __name__ == "__main__":
    text_file_name = sys.argv[1]
    decode_bin(text_file_name)
    
    
