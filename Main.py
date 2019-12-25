from PyQt5.QtWidgets import QApplication, QWidget, QGridLayout, QDesktopWidget, QMainWindow, QAction
from view.OptionsWindow import OptionsWindow
from view.FractalWindow import FractalRenderer
from view.gradient import *
import fractal

'''
Central wiget: places the fractal renderer, layer window, and gradient together on screen
'''
class CentralWidget(QWidget):
    def __init__(self, fract):
        super().__init__()

        self.fract = fract
        self.current_layer = self.fract.get_layer(0)
        self.initUI()

    def colorAddedCallback(self, event):
        self.current_layer.palette.add_color((255, 255, 255), event["location"])
        self.fractRenderer.update()

    def colorRemovedCallback(self, event):
        print("deleted")
        print(self.current_layer.palette.remove_color(event["location"]))
        self.fractRenderer.update()

    def colorMovedCallback(self, event):
        print("moved callback")
        pal = self.current_layer.palette
        if pal.remove_color(event["old_location"]):
            pal.add_color((255, 255, 255), event["location"])
        self.fractRenderer.update()

    def initUI(self):
        grid = QGridLayout()
        self.setLayout(grid)

        self.fractRenderer = FractalRenderer(self.fract)
        grid.addWidget(self.fractRenderer, 0, 0, 1, 1)

        options = OptionsWindow(self.fractRenderer)
        grid.addWidget(options, 0, 1, 1, 1)

        gradient = Gradient()
        gradient.registerColorAddCallback(self.colorAddedCallback)
        gradient.registerColorRemovedCallback(self.colorRemovedCallback)
        gradient.registerColorMovedCallback(self.colorMovedCallback)
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
        layer = fractal.HistogramLayer()
        self.fract.insert_layer(0, layer)
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