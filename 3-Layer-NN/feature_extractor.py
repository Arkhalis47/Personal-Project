from PIL import Image
import numpy as np
from scipy.signal import convolve2d
import math

def feature_extractor(image_filename):
    image = Image.open(image_filename)
    
    ###resize it
    image = image.resize((28,28))

    ##convert it to grayscale and into array
    image = np.array(image.convert("L"))

    ##try to remove the noise added
    image = gaussian_blur(image,1,(3,3))

    ##Extract edges with sobel
    image = sobel_and_angle_calculation(image)[0]

    ##threshold to binarize the image
    image = binarize(image, 90)

    ##apply dwt to get ll band
    image = dwt_ll(image, 2)

    ##flatten it
    return np.ndarray.flatten(image)

def dwt_ll(image, levels):
    image = image.astype(float) ##because we are performing averaging
    for _ in range(levels):
        image = dwt_ll_helper(image)
    return image

def dwt_ll_helper(image):
    result = np.zeros((image.shape[0]//2, image.shape[1]//2))

    ##perform dwt on row
    for row in range(len(image)):
        for col in range(0,len(image[0]),2):
            image[row][col//2] = (image[row][col] + image[row][col+1]) / 2
    
    ##perform dwt on col
    for col in range(len(image[0])//2):
        for row in range(0,len(image),2):
            result[row//2][col] = (image[row][col] + image[row+1][col]) / 2
    
    return result


def sobel_and_angle_calculation(image_array):
    ##prepare the kernel 
    Kx = np.matrix('-1, 0, 1; -2, 0, 2;, -1, 0, 1')
    Ky = np.matrix('-1, -2, -1; 0, 0, 0;, 1, 2, 1')

    ##Apply kernel to the image
    Gx = convolve2d(image_array, Kx, "same")
    Gy = convolve2d(image_array, Ky, "same")
    magnitude_array = np.sqrt(Gx**2 + Gy**2)

    ##now to get the angle array
    angle_array = np.arctan2(Gy, Gx) * (180/math.pi) ##since the output is in radian, we need to convert them to angle

    ##to convert to positive angle
    filter_angle = np.vectorize(convert_to_positive_angle)
    angle_array = filter_angle(angle_array)

    return magnitude_array.astype(np.uint8), angle_array.astype(np.uint8)

def convert_to_positive_angle(item):
    if item > 90:
        return 450 - item
    else:
        return 90 - item

def binarize(image, threshold_value):
    convert_binary = np.vectorize(thresholding)
    binary_image = convert_binary(image, threshold_value)
    return binary_image

def thresholding(item,threshold_value):
    if item < threshold_value:
        return 0
    else:
        return 1
    
def generate_gaussian_kernel(sigma : float, size : tuple):
    assert size[0] % 2 == 1 and size[1] % 2 == 1 and size[0] == size[1]
    ##size[0] is for row, size[1] is for col
    kernel = [None] * size[0]
    for i in range(len(kernel)):
        kernel[i] = [None] * size[1]
    
    sum_kernel = 0

    ##the calculation starts here
    first_part = (1 / (2 * math.pi * (sigma ** 2)))
    offset = ((size[0]-1)//2)
    for y in range(-offset, offset+1):
        for x in range(-offset, offset+1):
            second_part = ((x**2)+(y**2))/(2*(sigma**2))
            second_part = math.exp(-second_part)
            result =  first_part * second_part
            kernel[y+offset][x+offset] = result
            sum_kernel += result
    
    
    # now to normalize the kernel
    for y in range(size[0]):
        for x in range(size[1]):
            kernel[y][x] = round((kernel[y][x] / sum_kernel), 3)
    
    return kernel

def gaussian_blur(image_array, sigma, kernel_size):
    gaussian_kernel = generate_gaussian_kernel(sigma,kernel_size)
    filtered_image = convolve2d(image_array, gaussian_kernel, "same")

    ##change it to integer
    filtered_image = filtered_image.astype(np.uint8)
    return filtered_image