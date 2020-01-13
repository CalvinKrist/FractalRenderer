from PyQt5.QtWidgets import QWidget
from PyQt5.QtGui import QPainter, QImage, QPixmap
from PyQt5.QtCore import Qt
from Messenger import messenger


class FractalRenderer(QWidget):

    def __init__(self, fract):
        super().__init__()

        self.fract = fract
        self.initUI()

        # Set up to handle keyboard input
        messenger.subscribe("key_pressed", self.key_pressed)
        self.zoom_in_keys = [Qt.Key_Plus, Qt.Key_Equal, Qt.Key_PageUp, Qt.Key_Z]
        self.zoom_out_keys = [Qt.Key_Minus, Qt.Key_PageDown, Qt.Key_X]
        self.zoom_factor = 0.5
        self.inv_zoom_factor = 1 / self.zoom_factor

        # Handle mouse wheel input
        messenger.subscribe("mouse_wheel_moved", self.mouse_wheel_moved)

    def initUI(self):
        self.setMinimumSize(800, 650)
        self.fract.width = 800
        self.fract.height = 650
        self.qp = QPainter()

    def paintEvent(self, event):
        self.fract.width = self.width()
        self.fract.height = self.height()
        image = self.fract.render()

        self.qp.begin(self)
        image = QImage(bytes(image), self.fract.width, self.fract.height, self.fract.width * 3, QImage.Format_RGB888)
        pix = QPixmap(image)

        self.qp.drawPixmap(self.rect(), pix)
        self.qp.end()

    # Move viewport when fractal is clicked
    def mouseReleaseEvent(self, e):
        # Normalize x to -0.5 and 0.5
        rx = (e.x() / self.width() - 0.5)
        # Scale to viewport width and offset by current position
        rx = rx * self.fract.viewport_width + self.fract.x

        # Normalize y to -0.5 and 0.5
        ry = (e.y() / self.height() - 0.5)
        # Scale to viewport height and offset by current position
        ry = ry * self.height() / self.width() * self.fract.viewport_width + self.fract.y

        self.fract.x = rx
        self.fract.y = ry
        self.update()

    def key_pressed(self, event):
        if self.fract.layer_count() > 0:
            event = event["event"]

            # Handle zoom in
            if event.key() in self.zoom_in_keys:
                self.fract.viewport_width *= self.zoom_factor
                self.update()
            # Handle zoom out
            elif event.key() in self.zoom_out_keys:
                self.fract.viewport_width *= self.inv_zoom_factor
                self.update()

    def mouse_wheel_moved(self, event):
        if self.fract.layer_count() > 0:
            event = event["event"]

            if event.angleDelta().y() > 0:
                self.fract.viewport_width *= self.zoom_factor
                self.update()
            elif event.angleDelta().y() < 0:
                self.fract.viewport_width *= self.inv_zoom_factor
                self.update()
