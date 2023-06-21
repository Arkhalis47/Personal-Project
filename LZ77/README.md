# Encoder and Decoder with LZ77
An assignment on creating an encoder and decoder for asc file implementing LZ77

You can test out the script with the following commands:

To zip an asc file
python myzip.py <FILENAME> <WINDOW_BUFFER> <LOOK_AHEAD_COUNT>

Eg
python myzip.py secret.asc 6 4

To unzip the binary file
python myunzip.py <BINARY_FILENAME>

Eg
python myunzip.py secret.asc.bin


