- An ASCII character in 8-bit ASCII encoding is 8 bits (1 byte), though it can fit in 7 bits.
- An ISO-8895-1 character in ISO-8859-1 encoding is 8 bits (1 byte).
- A Unicode character in UTF-8 encoding is between 8 bits (1 byte) and 32 bits (4 bytes).
- A Unicode character in UTF-16 encoding is between 16 (2 bytes) and 32 bits (4 bytes), though 
  most of the common characters take 16 bits. This is the encoding used by Windows internally.
- A Unicode character in UTF-32 encoding is always 32 bits (4 bytes).
- An ASCII character in UTF-8 is 8 bits (1 byte), and in UTF-16 - 16 bits.
- The additional (non-ASCII) characters in ISO-8895-1 (0xA0-0xFF) would take 16 bits in 
  UTF-8 and UTF-16.
- That would mean that there are between 0.03125 and 0.125 characters in a bit.
