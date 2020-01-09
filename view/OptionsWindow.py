from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QHBoxLayout, QPushButton, QFrame
from PyQt5.QtGui import QFont, QIcon, QPainter
from PyQt5.QtCore import Qt
from Messenger import messenger


class LayerView(QWidget):

    def __init__(self, name):
        super().__init__()

        self.initUI(name)

    def visibility_toggled(self):
        self.checked = not self.checked
        icon = self.visible_icon if self.checked else QIcon()
        self.toggle_visible.setIcon(icon)

        if self.checked:
            self.toggle_visible.setStyleSheet('border:none')
        else:
            self.toggle_visible.setStyleSheet('border:black')

        messenger.publish("layer_gui_toggled", {"value" : self.checked, "elem": self})

    def initUI(self, name):
        self.setMinimumSize(240, 40)
        self.setMaximumWidth(300)
        self.font = QFont()
        self.font.setFamily("Arial")
        self.font.setPointSize(14)

        layout = QHBoxLayout()
        self.setLayout(layout)
        layout.setContentsMargins(0,0,0,0)

        # Add toggle visibility button
        self.toggle_visible = QPushButton()
        self.checked = True
        self.toggle_visible.setFlat(True)
        self.visible_icon = QIcon("resources/visible.png")
        self.toggle_visible.setIcon(self.visible_icon)
        self.toggle_visible.setMaximumWidth(18)
        self.toggle_visible.clicked.connect(self.visibility_toggled)

        self.toggle_visible.setStyleSheet("background-color: rgb(205, 205, 205);")
        layout.addWidget(self.toggle_visible)

        # Add layer name
        label = QLabel(name)
        label.setFont(self.font)
        layout.addWidget(label)

        # Add settings button
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

        # Setup behavior when layer is clicked
        selected_action = lambda event: messenger.publish("layer_gui_selected", {"elem": self})
        self.mouseReleaseEvent = selected_action
        selected_action(None)


class OptionsWindow(QFrame):

    def __init__(self, fractalWindow):
        super().__init__()
        self.fractalWindow = fractalWindow

        messenger.subscribe("delete_gui_clicked", self.remove_layer)
        messenger.subscribe("layer_gui_selected", self.layer_selected)
        messenger.subscribe("layer_gui_toggled", self.forward_layer_toggle_event)

        self.selected_layer = None

        self.initUI()

    def forward_layer_toggle_event(self, event):
        event["index"] = self.gui_index_to_fractal_index(event["elem"])
        del event["elem"]
        messenger.publish("layer_toggled", event)

    def gui_index_to_fractal_index(self, elem):
        layer_index = self.layout.count() - self.layout.indexOf(elem) - 1
        if self.layout.indexOf(elem) == -1:
            layer_index -= 1
        return layer_index

    def layer_selected(self, event):
        if self.selected_layer:
            self.selected_layer.setStyleSheet("background-color: white")
        event["elem"].setStyleSheet("background-color: rgb(205, 205, 205)")
        self.selected_layer = event["elem"]

        layer_index = self.gui_index_to_fractal_index(event["elem"])
        messenger.publish("selected_layer_changed", {"index": layer_index})


    def remove_layer(self, event):
        layer_index = self.gui_index_to_fractal_index(event["elem"])
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
