from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QHBoxLayout, QFrame, QCheckBox, QPushButton
from PyQt5.QtGui import QFont, QIcon
from PyQt5.QtCore import Qt

from model.Layer import Layer

class LayerView(QWidget):
    def __init__(self, name):
        super().__init__()

        self.layer = Layer()

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
        delete.setIcon(delIcon)
        delete.setMaximumWidth(30)
        delete.setStyleSheet("background-color: white; shadow: none")
        layout.addWidget(delete)


class OptionsWindow(QFrame):
    def __init__(self, fractalWindow):
        super().__init__()
        self.fractalWindow = fractalWindow

        self.initUI()

        # Add default layer
        self.add_layer()

    def add_layer(self):
        # Add layer just below 'New Layer' button
        name = "Layer " + str(self.layout.count())
        self.layout.insertWidget(1, LayerView(name), alignment=Qt.AlignCenter)

    def initUI(self):
        # Configure layout
        self.layout = QVBoxLayout()
        self.setLayout(self.layout)
        self.layout.setAlignment(Qt.AlignBottom)

        # Configure style
        self.setMaximumWidth(300)
        self.setStyleSheet("background-color: white")
        self.setFrameStyle(QFrame.Box | QFrame.Raised)

        # Add 'New Layer' button
        newLayerIcon = QIcon("resources/newLayer.png")
        newLayer = QPushButton()
        newLayer.clicked.connect(self.add_layer)
        newLayer.setIcon(newLayerIcon)
        newLayer.setMaximumWidth(30)
        newLayer.setStyleSheet("background-color: white; shadow: none")
        self.layout.insertWidget(0, newLayer, alignment = Qt.AlignCenter)
