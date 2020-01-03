from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QHBoxLayout, QFrame, QCheckBox, QPushButton
from PyQt5.QtGui import QFont, QIcon
from PyQt5.QtCore import Qt
from Messenger import messenger


class LayerView(QWidget):
    def __init__(self, name):
        super().__init__()

        self.initUI(name)

    def initUI(self, name):
        self.setMinimumSize(240, 40)
        self.setMaximumWidth(300)
        #self.setStyleSheet("border: 1px solid red")
        self.setStyleSheet("background-color: white")
        self.font = QFont()
        self.font.setFamily("Arial")
        self.font.setPointSize(14)

        layout = QHBoxLayout()
        self.setLayout(layout)
        layout.setContentsMargins(0,0,0,0)

        label = QLabel(name)
        label.setFont(self.font)

        box = QCheckBox()
        box.setMaximumWidth(18)
        layout.addWidget(box)
        layout.addWidget(label)

        # Add dropdown button
        settingsIcon = QIcon("resources/settings.png")
        settings = QPushButton()
        settings.setIcon(settingsIcon)
        settings.setMaximumWidth(30)
        settings.setStyleSheet("background-color: white; shadow: none")
        layout.addWidget(settings)

        # Add delete button
        delIcon = QIcon("resources/delete.png")
        delete = QPushButton()
        delete.clicked.connect(lambda: messenger.publish("delete_gui_clicked", {"elem": self}))
        delete.setIcon(delIcon)
        delete.setMaximumWidth(30)
        delete.setStyleSheet("background-color: white; shadow: none")
        layout.addWidget(delete)

        # Setup click behavior
        selected_action = lambda event: messenger.publish("layer_gui_selected", {"elem": self})
        self.mouseReleaseEvent = selected_action
        selected_action(None)


class OptionsWindow(QFrame):

    def __init__(self, fractalWindow):
        super().__init__()
        self.fractalWindow = fractalWindow

        messenger.subscribe("delete_gui_clicked", self.remove_layer)
        messenger.subscribe("layer_gui_selected", self.layer_selected)

        self.selected_layer = None

        self.initUI()

    def layer_selected(self, event):
        event["elem"].setStyleSheet("background-color: blue; shadow: none")
        if self.selected_layer:
            self.selected_layer.setStyleSheet("background-color: white; shadow: none")
        self.selected_layer = event["elem"]


        layer_index = self.layout.count() - self.layout.indexOf(event["elem"]) - 1
        if self.layout.indexOf(event["elem"]) == -1:
            layer_index -= 1
        messenger.publish("selected_layer_changed", {"index": layer_index})


    def remove_layer(self, event):
        layer_index = self.layout.count() - self.layout.indexOf(event["elem"]) - 1
        if event["elem"] is self.selected_layer:
            self.selected_layer = None
        event["elem"].deleteLater()
        messenger.publish("layer_removed", {"index": layer_index})

    def add_layer(self):
        # Add layer just below 'New Layer' button
        position = self.layout.count() - 1
        messenger.publish("layer_added", {"index": position})
        name = "Layer " + str(position + 1)
        self.layout.insertWidget(1, LayerView(name), alignment=Qt.AlignCenter)

    def initUI(self):
        # Configure layout
        self.layout = QVBoxLayout()
        self.setLayout(self.layout)
        self.layout.setAlignment(Qt.AlignBottom)

        # Configure style
        self.setMaximumWidth(300)
        self.setMinimumSize(260, 40)
        self.setStyleSheet("background-color: white")
        self.setFrameStyle(QFrame.Box | QFrame.Raised)

        # Add 'New Layer' button
        newLayerIcon = QIcon("resources/newLayer.png")
        self.newLayerIcon = newLayerIcon
        newLayer = QPushButton()
        newLayer.clicked.connect(self.add_layer)
        newLayer.setIcon(newLayerIcon)
        newLayer.setMaximumWidth(30)
        newLayer.setStyleSheet("background-color: white; shadow: none")
        self.layout.insertWidget(0, newLayer, alignment = Qt.AlignCenter)
