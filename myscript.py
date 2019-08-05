import abc
import module1
import module2

class myclas:
	def __init__(self, param1, param2):
		self.param1 = param1
		self.param2 = param2


c = myclas("Hello", "World")
print c.__dict__