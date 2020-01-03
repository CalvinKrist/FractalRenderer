class Messenger:

    def __init__(self):

        self.callbacks = {}

    def subscribe(self, tag, callback):
        if tag in self.callbacks:
            self.callbacks[tag].append(callback)
        else:
            self.callbacks[tag] = [callback]
            
    def publish(self, tag, message):
        if tag in self.callbacks:
            for callback in self.callbacks[tag]:
                callback(message)


messenger = Messenger()
