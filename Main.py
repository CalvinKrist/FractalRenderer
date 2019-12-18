from PyQt5.QtWidgets import QApplication, QWidget, QGridLayout, QDesktopWidget, QMainWindow, QAction
from view.OptionsWindow import OptionsWindow
from view.FractalWindow import FractalRenderer
from view.gradient import *

'''
Central wiget: places the fractal renderer, layer window, and gradient together on screen
'''
class CentralWidget(QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

    def initUI(self):
        grid = QGridLayout()
        self.setLayout(grid)

        fractal = FractalRenderer()
        grid.addWidget(fractal, 0, 0, 1, 1)

        options = OptionsWindow(fractal)
        grid.addWidget(options, 0, 1, 1, 1)

        gradient = Gradient()
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

        widget = CentralWidget()
        self.setCentralWidget(widget)

        self.setWindowTitle('FractalFun')
        self.show()

import fractal

def t(fract):

    layer = fractal.HistogramLayer()

    print(fract.layer_count())
    fract.insert_layer(0, layer)
    print(fract.layer_count())
    lNew = (fractal.HistogramLayer)(fract.remove_layer(0))
    t = lNew.test()
    print(lNew.get_opacity())
    print(fract.layer_count())

if __name__ == '__main__':
    '''app = QApplication(sys.argv)
    ex = Window()

    # Center the window
    qtRectangle = ex.frameGeometry()
    centerPoint = QDesktopWidget().availableGeometry().center()
    qtRectangle.moveCenter(centerPoint)
    ex.move(qtRectangle.topLeft())

    sys.exit(app.exec_())'''
    fract = fractal.Fractal()
    t(fract)