from PyQt5.QtWidgets import QApplication, QWidget, QGridLayout, QDesktopWidget, QMainWindow, QAction
from view.OptionsWindow import OptionsWindow
from view.FractalWindow import FractalRenderer
from view.gradient import *
import fractal
from Messenger import messenger


def hex_to_rgb(hex):
    hex = hex.lstrip("#")
    return tuple(int(hex[i:i + 2], 16) for i in (0, 2, 4))

def rgb_to_hex(rgb):
    return '#%02x%02x%02x' % (rgb[0], rgb[1], rgb[2])


'''
Central wiget: places the fractal renderer, layer window, and gradient together on screen
'''
class CentralWidget(QWidget):
    def __init__(self, fract):
        super().__init__()

        self.fract = fract
        self.current_layer = None
        self.initUI()

    def update_palette(self):
        self.gradient.interior_color = rgb_to_hex(self.current_layer.palette.interior_color)
        self.gradient._gradient = [(color_point[3], rgb_to_hex(color_point)) for color_point in self.current_layer.palette.get_colors()]
        self.gradient.update()

    def color_added_callback(self, event):
        if self.current_layer:
            self.current_layer.palette.add_color(hex_to_rgb(event['color']), event["location"])
            self.fractRenderer.update()

    def color_removed_callback(self, event):
        if self.current_layer:
            self.current_layer.palette.remove_color(event["location"])
            self.fractRenderer.update()

    def color_moved_callback(self, event):
        if self.current_layer:
            pal = self.current_layer.palette
            if pal.remove_color(event["old_location"]):
                pal.add_color(hex_to_rgb(event['color']), event["location"])

            self.fractRenderer.update()

    def color_changed_callback(self, event):
        if self.current_layer:
            pal = self.current_layer.palette
            if pal.remove_color(event["location"]):
                pal.add_color(hex_to_rgb(event['color']), event["location"])
            self.fractRenderer.update()

    def interior_color_changed_callback(self, event):
        if self.current_layer:
            pal = self.current_layer.palette
            pal.interior_color = hex_to_rgb(event['color'])
            self.fractRenderer.update()

    def layer_added_callback(self, event):
        self.current_layer = fractal.HistogramLayer()
        self.fract.insert_layer(event["index"], self.current_layer)
        self.update_palette()
        self.fractRenderer.update()

    def layer_removed_callback(self, event):
        self.fract.remove_layer(event["index"])
        self.current_layer = None
        self.fractRenderer.update()

    def selected_layer_changed(self, event):
        print(event)
        self.current_layer = self.fract.get_layer(event["index"])
        self.update_palette()
        self.fractRenderer.update()

    def initUI(self):
        grid = QGridLayout()
        self.setLayout(grid)

        self.fractRenderer = FractalRenderer(self.fract)
        grid.addWidget(self.fractRenderer, 0, 0, 1, 1)

        options = OptionsWindow(self.fractRenderer)
        grid.addWidget(options, 0, 1, 1, 1)

        # Setup callbacks for OptionsView
        messenger.subscribe("layer_added", self.layer_added_callback)
        messenger.subscribe("selected_layer_changed", self.selected_layer_changed)

        # Create the gradient
        gradient = Gradient()
        self.gradient = gradient
        messenger.subscribe("color_added", self.color_added_callback)
        messenger.subscribe("color_removed", self.color_removed_callback)
        messenger.subscribe("color_moved", self.color_moved_callback)
        messenger.subscribe("color_changed", self.color_changed_callback)
        messenger.subscribe("interior_color_changed", self.interior_color_changed_callback)
        messenger.subscribe("layer_removed", self.layer_removed_callback)
        gradient.setMaximumHeight(100)
        grid.addWidget(gradient, 1, 0, 1, 2)

        self.setLayout(grid)

'''
The main Window object for the program.
Contains the top file bar and the central widget
'''
class Window(QMainWindow):

    def __init__(self):
        super().__init__()
        self.fract = fractal.Fractal()
        self.initUI()

    def initUI(self):

        # Add main manu bar
        exitAct = QAction('&Exit', self)
        exitAct.setShortcut('Ctrl+Q')
        exitAct.setStatusTip('Exit application')

        self.statusBar()

        menubar = self.menuBar()
        fractalMenu = menubar.addMenu('Fractal')
        fractalMenu.addAction(exitAct)

        networkMenu = menubar.addMenu('Network')
        networkMenu.addAction(exitAct)

        widget = CentralWidget(self.fract)
        self.setCentralWidget(widget)

        self.setWindowTitle('FractalFun')
        self.show()

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = Window()

    # Center the window
    qtRectangle = ex.frameGeometry()
    centerPoint = QDesktopWidget().availableGeometry().center()
    qtRectangle.moveCenter(centerPoint)
    ex.move(qtRectangle.topLeft())

    sys.exit(app.exec_())