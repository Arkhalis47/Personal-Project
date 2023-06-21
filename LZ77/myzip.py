import sys

def write_into_bin_file(byte_array, filename):
    filename = filename + ".bin"
    output_file = open(filename, 'wb')
    output_file.write(byte_array)
    output_file.close()

def compress_txt_file(txt_file_name, window_limit_size, lookahead_limit_size):
    text_sample = read_input(txt_file_name)
    freq_array = find_frequency_of_ascii(text_sample)
    huffman_encoded_array = huffman_encode(freq_array)
    output_byte_array = bytearray()
    output_formatted_bit = []

    #header_encoding

    #file_length
    file_length_encoding = elias(len(txt_file_name))
    output_formatted_bit.append(file_length_encoding)

    #each of the ascii in the filename
    for char in txt_file_name:
        output_formatted_bit.append(convert_ascii_to_bit(ord(char)))

    #encode the number of character in the file
    output_formatted_bit.append(elias(len(text_sample)))

    #encode the number of distict char in the file
    no_uniq_char = find_distict_char(freq_array)
    output_formatted_bit.append(elias(no_uniq_char))

    #encode the distinct character with their huffman encoding
    output_formatted_bit = find_huffman_encode(huffman_encoded_array, output_formatted_bit)


    #data encoding 
    lz77_output = lz77(text_sample, window_limit_size, lookahead_limit_size)
    lz77_modified = format_lz77(lz77_output, huffman_encoded_array)

    for triple in lz77_modified:
        for formatted_bit in triple:
            output_formatted_bit.append(formatted_bit)
    

    #now to throw all these to bytearray

    #combine all the bits 
    
    concat_bits = [0,0]
    for bit_index in range(len(output_formatted_bit)):
        
        concat_bits = combine_bits(concat_bits, output_formatted_bit[bit_index])

        #if the length of the bits are more than 8 bits, append them into byte array
        total_bit_length = get_bit_length(concat_bits[0]) + concat_bits[1]

        if total_bit_length >= 8:
            concat_bits, output_byte_array = take_away_8bit(concat_bits, output_byte_array)

    #cases where i still have more than 8 bits after the last iteration
    total_bit_length = get_bit_length(concat_bits[0]) + concat_bits[1]
    while total_bit_length >= 8:
        concat_bits, output_byte_array = take_away_8bit(concat_bits, output_byte_array)
        total_bit_length = get_bit_length(concat_bits[0]) + concat_bits[1]

    #still have some bits, now to pad extra 0s
    total_bit_length = get_bit_length(concat_bits[0]) + concat_bits[1]
    if total_bit_length > 0 :
        concat_bits[0] = concat_bits[0] << (8 - total_bit_length) #pad extra 0 to the left
        output_byte_array.append(concat_bits[0])

    #write into binary file
    write_into_bin_file(output_byte_array,txt_file_name)
    


def find_huffman_encode(huffman_encoded_array, output_formatted_bit):
    for array in range(len(huffman_encoded_array)):
        if huffman_encoded_array[array] == None:
            continue
        else:
            #the encoding of ascii bit
            character_bit = convert_ascii_to_bit(array)
            output_formatted_bit.append(character_bit)

            #the encoding of the length of the huffman encoding
            length_of_bit = get_bit_length(huffman_encoded_array[array][0]) + huffman_encoded_array[array][1]
            length_bit = elias(length_of_bit)
            output_formatted_bit.append(length_bit)

            #the huffman encoding
            encoded_char_bit = huffman_encoded_array[array]
            output_formatted_bit.append(encoded_char_bit)
    
    return output_formatted_bit


def find_distict_char(frequency_array):
    ret_val = 0
    for i in frequency_array:
        if i > 0:
            ret_val += 1
    return ret_val

def read_input(file_name):
    txtfile = open(file_name,'r')
    txt = txtfile.read()
    txtfile.close()
    return txt

def find_frequency_of_ascii(txt_from_file):
    frequency_table = [0] * 256
    for char in txt_from_file:
        frequency_table[ord(char)] += 1
    return frequency_table
    

def convert_ascii_to_bit(ascii_number):
    length_of_number = get_bit_length(ascii_number)
    number_of_0_padded = 8 - length_of_number
    return [ascii_number,number_of_0_padded]

def get_bit_length(number):
    '''
    Get the bit length of a value (in binary)
    '''
    length_of_bit = 0
    while number > 0:
        length_of_bit += 1
        number = number // 2
    return length_of_bit

def format_lz77(output_from_lz77, huffman_encoding_array):
    for index in range(len(output_from_lz77)):
        array_to_format = output_from_lz77[index]
        array_to_format[0] = elias(array_to_format[0])
        array_to_format[1] = elias(array_to_format[1])
        array_to_format[2] = huffman_encoding_array[ord(array_to_format[2])]

    return output_from_lz77

def lz77(txt_from_file, window_limit_size, lookahead_limit_size):
    output_array = []
    output_array.append([0,0,txt_from_file[0]])
    window_pointer = 0
    window_size = 1
    
    done = False

    while not done:

        #i am doing this because i don't have time, the better version is to modify z_algo to use pointer
        win_look_slice = txt_from_file[window_pointer: min(window_pointer + window_size + lookahead_limit_size, len(txt_from_file))]
        look_slice = txt_from_file[window_pointer+window_size : min(window_pointer + window_size + lookahead_limit_size, len(txt_from_file))]
        
        input_for_z = look_slice + win_look_slice

        z_array = z_algo(input_for_z)
        
        index_to_start = len(look_slice)


        max_index, number_to_look_back = find_max(z_array, index_to_start, window_size, lookahead_limit_size)


        number_to_copy = min(z_array[max_index], lookahead_limit_size)

        if number_to_copy < 1:
            number_to_look_back = 0
        
        next_char = txt_from_file[window_pointer+window_size + number_to_copy] 

        #initialization of first formatted lz77 array, append to the output array
        output_array.append([number_to_look_back,number_to_copy, next_char])


        
        #update window size and window_pointer
        start_of_lookahead = window_pointer+window_size + number_to_copy + 1

        if start_of_lookahead == (len(txt_from_file) -1):
            output_array.append([0,0,txt_from_file[start_of_lookahead]])
            done = True
        
        elif start_of_lookahead < len(txt_from_file):
            #take the max because we need to take into account when window size for the next iteration is less than the window size limit
            window_pointer = max(start_of_lookahead-window_limit_size, window_pointer)

            window_size = start_of_lookahead - window_pointer
        
        else:
            done = True


    return output_array



def find_max(z_array, index_to_start, window_size, lookahead_limit_size):
    max_value = 0
    max_index = None
    number_to_look_back = window_size 
    saved_number = 0
    pointer = index_to_start

    while pointer < (index_to_start + window_size):

        #prioritise the copy from index closer to lookahead buffer, thus >= and not >
        if z_array[pointer] >= max_value:

            if z_array[pointer] > lookahead_limit_size:
                max_value = lookahead_limit_size
            else:
                max_value = z_array[pointer]

            saved_number = number_to_look_back
            max_index = pointer
            
        number_to_look_back -=1
        pointer += 1
        
    return max_index, saved_number


    
def elias(number_to_encode):
    number_offset = number_to_encode + 1
    bit_pointer = get_bit_length(number_offset) # bit pointer stores how many bits this encoding uses
    combined_bits = number_offset #init value, the number to encode

    while number_offset != 1:
        length_of_offset = get_bit_length(number_offset) - 1 #feature of elias
        

        #Now trying to encode the length of this in the next loop
        number_offset = length_of_offset

        #do flipping of MSB here
        value_to_minus = 2 ** (get_bit_length(length_of_offset)-1)
        padded_offset = length_of_offset - value_to_minus

        #Padding of bits to the left
        padded_offset = padded_offset << bit_pointer #pad to left, for concatenation
        bit_pointer += get_bit_length(length_of_offset) #add the value of bit pointer

        #OR operation to concatenate bits
        combined_bits = padded_offset | combined_bits

    number_of_0_padded = bit_pointer - get_bit_length(combined_bits)
    return [combined_bits, number_of_0_padded]


def combine_bits(bit_1,bit_2):
    '''
    A function to combine 2 bits together
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


def take_away_8bit(bit, byte_array):
    '''
    A function to take away the first 8 bits and append it to the bytearray
    Make sure that input must be in this form 
    [value, number of zeros padded to the left]
    '''
    number = bit[0]
    total_bits = bit[1] + get_bit_length(number)

    #remove the remaining bits that we want to continue with
    temp = number >> (total_bits - 8)

    #add to byte array
    byte_array.append(temp)

    #prepare for XOR operation
    temp = temp << (total_bits - 8)

    #take away the first 8 bits with XOR operation
    number = number ^ temp
    bits_remaining = total_bits - 8
    number_of_0_padded = bits_remaining - get_bit_length(number)
    return [number,number_of_0_padded], byte_array



def huffman_encode(uniq_char_with_freq):
    array_of_node = []
    array_of_encoded_char = [None] * len(uniq_char_with_freq) #array of encoded character, if the character is present in the text file

    for i in range(len(uniq_char_with_freq)):
        uniq_char_freq = uniq_char_with_freq[i]
        if uniq_char_freq > 0: #there is this character in the text file
            new_node = Node(i, uniq_char_freq)
            array_of_node.append(new_node)

            array_of_encoded_char[i] = [0,0]

    huffman_min_heap = Min_Heap(array_of_node, array_of_encoded_char)
    encoded_array = huffman_min_heap.encode()

    for i in range(len(encoded_array)) :
        if encoded_array[i] == None:
            continue
        else:
            encoded_array[i] = reverse_binary(encoded_array[i])

    return encoded_array

 

def reverse_binary(bit):
    ##refered to google https://algorithms.tutorialhorizon.com/reverse-the-binary-representation-of-a-number/
    ##but i did understand why the algorithm work, explaination is below
    value = bit[0]
    total_bits = get_bit_length(value) + bit[1]
    reversed_value = 0
    while value > 0:
        reversed_value = reversed_value << 1 # pad to left 1 time to concatenate with the last bit of the current value

        #bitwise &, since 1 & 1 will give 1, others 0, this will ensure that only if the last bit of value is 1, it will return 1
        reversed_value = reversed_value | (value & 1) # concatenation always use OR

        value = value >> 1 #pad to right, eliminating the last bit that we already examined/processed
    
    reversed_value = reversed_value << bit[1]
        
    number_of_0_padded = total_bits - get_bit_length(reversed_value)
    return [reversed_value,number_of_0_padded]

class Node:
    def __init__(self, char, frequency):
        self.char = [char]
        self.frequency = frequency

class Min_Heap:
    def __init__(self, array_of_distict_char, array_of_encoded_char):
        '''
        Constructor for the min heap
        '''
        self.array = [None] * (len(array_of_distict_char) + 1)
        self.length = len(self.array)-1
        self.encoded = array_of_encoded_char

        for i in range(1, len(self.array)):
            self.array[i] = array_of_distict_char[i-1]

        self.heapify()

    def heapify(self):
        node_index_to_sink = self.length // 2
        while node_index_to_sink > 0:
            self.sink(node_index_to_sink)
            node_index_to_sink -= 1
        
    def is_empty(self):
        return self.length == 0

    def encode(self):
        finish_encode = False

        if self.length == 1:
            node_to_encode = self.array[1]
            for index in node_to_encode.char:
                self.encoded[index] = [0,1]

        else:
            while not finish_encode:
                node_1 = self.serve()
                node_2 = self.serve()

                
                for index_1 in node_1.char: #append 0 to the first character encoded
                    previous_value = self.encoded[index_1][0] 
                    pre_modified_length = get_bit_length(previous_value) #how many bits does this value have
                    previous_value = previous_value << 1 #concat 0 to the right

                    #if no changes, that means we are having this scenario of padding 0 when the original value is 0
                    if get_bit_length(previous_value) == pre_modified_length: 
                        self.encoded[index_1][1] += 1
                    
                    self.encoded[index_1][0] = previous_value #save the result
                    


                for index_2 in node_2.char:
                    previous_value = self.encoded[index_2][0]
                    previous_value = previous_value << 1
                    previous_value = previous_value | 1 #concat 1 to the right

                    self.encoded[index_2][0] = previous_value  #save the result

                    node_1.char.append(index_2) #node_1 now is the combination of node 1 and node 2
                
                node_1.frequency += node_2.frequency #add the frequency of node 2 into node 1

                if self.length == 0:
                    finish_encode = True
                
                else:
                    self.add(node_1) # add back into the heap
            
            
        return self.encoded


    def add(self, node_to_add):
        '''
        Add the node into the heap
        '''
        self.length += 1
        self.array[self.length] = node_to_add
        self.rise(self.length)

    def serve(self):
        '''
        A typical serve operation of a heap
        '''
        node_to_serve = self.array[1]
        self.swap_and_update(1,self.length)
        self.length -= 1
        self.sink(1)
        return node_to_serve

    def rise(self,node_index):
        '''
        The rise operation for a standard min heap

        Complextiy
        Time: O(log V) where V is the total number of vertices in the min heap array
        Space: O(1) for constant auxiliary space used
        '''
        done = False
        while node_index > 1 and not done:
            if self.array[node_index//2].frequency > self.array[node_index].frequency:
                self.swap_and_update(node_index//2, node_index)
                node_index = node_index // 2
            elif self.array[node_index//2].frequency == self.array[node_index].frequency and len(self.array[node_index//2].char) > len(self.array[node_index].char):
                self.swap_and_update(node_index//2, node_index)
                node_index = node_index // 2
            else:
                done = True
        
            
    def sink(self, node_index):
        '''
        The sink operation for a standard min heap

        Complextiy
        Time: O(log V) where V is the total number of vertices in the min heap array
        Space: O(1) for constant auxiliary space used
        '''
        while node_index*2 <= self.length and self.array[node_index*2] != None: #at least has one child
            smallest_child = self.find_smallest_child(node_index)
            if self.array[node_index].frequency > self.array[smallest_child].frequency:
                self.swap_and_update(node_index, smallest_child)
                node_index = smallest_child
            elif self.array[node_index].frequency == self.array[smallest_child].frequency and len(self.array[node_index].char) > len(self.array[smallest_child].char):
                self.swap_and_update(node_index, smallest_child)
                node_index = smallest_child
            else:
                break


    def find_smallest_child(self, node_index):
        '''
        This function will be used in sink operation to find the smallest child, in order for the parent to check whether to sink further into the heap or not

        Complextiy
        Time: O(1) for comparison
        Space: O(1) for constant auxiliary space used
        '''
        if node_index*2 == self.length or self.array[(node_index*2)+1] == None or self.array[node_index*2].frequency < self.array[(node_index*2)+1].frequency:
            return node_index*2
        elif self.array[node_index*2].frequency == self.array[(node_index*2)+1].frequency and len(self.array[node_index*2].char) < len(self.array[(node_index*2)+1].char):
            return node_index*2
        else:
            return (node_index*2)+1


    def swap_and_update(self, index_1, index_2):
        '''
        This function will be used to swap the position of nodes in the heap

        '''
        vertex_1 = self.array[index_1]
        vertex_2 = self.array[index_2]
    
        self.array[index_1] = vertex_2
        self.array[index_2] = vertex_1

def z_algo(pattern):
    '''
    This function is the main function for the z's algorithm
    Input: String 
    Output: Z-array (Containing z values)
    '''
    z_array = [0]*len(pattern) #Creation of z array
    
    if len(pattern) > 1:
        z_array[0] = len(pattern) #Initialisation of the first z value which must be length of the pattern
        pointer, left_boundary, right_boundary = z_algo_initialization(pattern, z_array)  
    
        while pointer < len(pattern):

            if pointer > right_boundary: #Means we are out of the z box
                left_boundary, right_boundary = z_algo_condition_out_of_box(z_array,pointer,left_boundary,right_boundary,pattern)
            else: #Means we are in the z box
                remaining = right_boundary - pointer + 1 #Calculate the remaining character from pointer to the end of z box
                z_algo_condition_in_box(z_array, remaining, pointer, left_boundary, right_boundary, pattern)
            pointer += 1

    return z_array
    



def z_algo_initialization(pattern, z_array):
    '''
    This function will initialize z-value of the 2nd character in the input string, and subsequent character should the z_value more than 1
    Input: Z array, pattern
    Output: Updated z array
    '''
    z_value = 0
    reference = 0
    pointer = 1


    while pointer < len(pattern) and pattern[reference] == pattern[pointer]:
        z_value += 1
        reference += 1
        pointer += 1
    z_array[1] = z_value


    if z_value > 0:
        left_boundary = 1
        pointer = 2
        while z_value != 0 and pointer < len(pattern):
            z_array[pointer] = z_value - 1
            pointer += 1
            z_value -= 1
        right_boundary = pointer - 2
    else:
        pointer += 1
        left_boundary = 0
        right_boundary = 0


    return pointer, left_boundary, right_boundary
    

def z_algo_condition_out_of_box(z_array,pointer,left_boundary,right_boundary,pattern):
    '''
    This function will deal with the scenario when the pointer is out of the z box, doing explicit comparison
    The left and right boundary are here in case we need to update them
    Input: Z array, pointer, left boundary of z box, right boundary of z box, the pattern
    Output: The left and right boundary index
    '''
    reference = 0
    temp_pointer = pointer
    z_value = 0
    while temp_pointer < len(pattern) and pattern[temp_pointer] == pattern[reference]:
        z_value += 1
        temp_pointer += 1
        reference += 1
    z_array[pointer] = z_value
    
    if z_value > 0:
        left_boundary = pointer
        right_boundary = left_boundary + z_value - 1
    
    return left_boundary,right_boundary


def z_algo_condition_in_box(z_array,remaining,pointer,left_boundary,right_boundary,pattern):
    '''
    This function will deal with the scenario when the pointer is in the z box, and update the z array accordingly
    Input: Z array, the remaining number of character from pointer to the right boundary, pointer, left boundary, right boundary, pattern
    Output: No output
    '''
    previous_z_value = z_array[pointer-left_boundary]

    #Case 1 previous z_value is more than remaining, bounded by remaining
    if previous_z_value > remaining:
        z_array[pointer] = remaining

    #Case 2 previous z_value is less than remaning, that means it will be the same 
    elif previous_z_value < remaining:
        z_array[pointer] = previous_z_value


    #Case 3 previous z_value == remaining, that means have to compare the character outside the z_box
    else:
        z_value = previous_z_value
        check_pointer = right_boundary + 1
        reference = remaining
        while check_pointer < len(pattern) and pattern[check_pointer] == pattern[reference]:
            z_value += 1
            check_pointer += 1
            reference += 1
        z_array[pointer] = z_value


if __name__ == "__main__":
    text_file_name = sys.argv[1]
    window_buffer = sys.argv[2]
    lookahead_buffer = sys.argv[3]
    compress_txt_file(text_file_name,int(window_buffer),int(lookahead_buffer))