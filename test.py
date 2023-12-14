a = b'a12345'
b = b'123456'

print(a, type(a))
print(b, type(b))

c = int.from_bytes(a, byteorder='big')
print(c, type(c))

d = input()
print(d, type(d))

e = d.encode('ascii')
print(e, type(e))