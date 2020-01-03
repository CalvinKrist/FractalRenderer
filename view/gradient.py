import sys
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtCore import Qt
from PyQt5.QtCore import pyqtSignal as Signal
from Messenger import messenger

class Gradient(QtWidgets.QWidget):

    gradientChanged = Signal()

    def __init__(self, gradient=None, *args, **kwargs):
        super().__init__(*args, **kwargs)

        self.setSizePolicy(
            QtWidgets.QSizePolicy.MinimumExpanding,
            QtWidgets.QSizePolicy.MinimumExpanding
        )

        if gradient:
            self._gradient = gradient

        else:
            self._gradient = [
                (0.0, '#000000'),
                (1.0, '#ffffff'),
            ]

        # Stop point handle sizes.
        self._handle_w = 10
        self._handle_h = 15

        self.left_start = self._handle_w / 2
        self.right_padding = 80
        self.right_end = self.width() - self.right_padding

        self._drag_position = None
        self._old_position = None

        self.interior_color = "#ffffff"

    def paintEvent(self, e):
        painter = QtGui.QPainter(self)
        self.right_end = self.width() - self.right_padding
        width = self.right_end - self.left_start
        height = painter.device().height()

        # Draw the linear horizontal gradient.
        gradient = QtGui.QLinearGradient(self.left_start, 0, width + self.left_start, 0)
        for stop, color in self._gradient:
            gradient.setColorAt(stop, QtGui.QColor(color))

        rect = QtCore.QRect(self.left_start, height * 0.25, width, height * 0.75)
        painter.fillRect(rect, gradient)

        # Draw the stop handles.
        for stop, color in self._gradient:
            painter.setBrush(QtGui.QBrush(QtGui.QColor(color), Qt.SolidPattern))

            translated_stop = stop * width + self.left_start
            rect = QtCore.QRect(
                translated_stop - self._handle_w / 2,
                0,
                self._handle_w,
                self._handle_h * 2 / 3
            )
            painter.drawRect(rect)

            points = [
                QtCore.QPoint(translated_stop - self._handle_w / 2, self._handle_h / 1.5),
                QtCore.QPoint(translated_stop + self._handle_w / 2, self._handle_h / 1.5),
                QtCore.QPoint(translated_stop, self._handle_h)

            ]
            painter.drawPolygon(QtGui.QPolygon(points))

        # Draw interior color
        painter.setBrush(QtGui.QBrush(QtGui.QColor(self.interior_color), Qt.SolidPattern))
        width = height * 0.5
        rect = QtCore.QRect(
            self.width() - self.right_padding / 2 - width / 2,
            height * 0.25 - 14,
            width,
            width
        )
        painter.drawRect(rect)
        painter.drawText(self.width() - self.right_padding / 2 - width / 2 + 4, height * 0.75, "Interior")
        painter.drawText(self.width() - self.right_padding / 2 - width / 2 + 10, height * 0.75 + 14, "Color")

        painter.end()

    def sizeHint(self):
        return QtCore.QSize(200, 80)

    def _sort_gradient(self):
        self._gradient = sorted(self._gradient, key=lambda g:g[0])

    def _constrain_gradient(self):
        self._gradient = [
            # Ensure values within valid range.
            (max(0.0, min(1.0, stop)), color)
            for stop, color in self._gradient
        ]

    def setGradient(self, gradient):
        assert all([0.0 <= stop <= 1.0 for stop, _ in gradient])
        self._gradient = gradient
        self._constrain_gradient()
        self._sort_gradient()
        self.gradientChanged.emit()

    def gradient(self):
        return self._gradient

    def addStop(self, stop, color=None):
        # Stop is a value 0...1, find the point to insert this stop
        # in the list.
        assert 0.0 <= stop <= 1.0

        index = -1
        for n, g in enumerate(self._gradient):
            if g[0] > stop:
                # Insert before this entry, with specified or next color.
                self._gradient.insert(n, (stop, color or '#ffffff'))
                index = n
                break
        if index == -1:
            # Insert at end of list
            index = len(self._gradient)
            self._gradient.append((stop, color or '#ffffff'))

        self._constrain_gradient()

        message = {"color" : self._gradient[index][1], "location": self._gradient[index][0]}
        messenger.publish("color_added", message)

        self.gradientChanged.emit()
        self.update()

    def removeStopAtPosition(self, n):
        if len(self._gradient) > 1:
            message = {"color": self._gradient[n][1], "location": self._gradient[n][0]}
            messenger.publish("color_removed", message)

        del self._gradient[n]
        self.gradientChanged.emit()
        self.update()

    def setColorAtPosition(self, n, color):
        if n < len(self._gradient):
            stop, _ = self._gradient[n]
            self._gradient[n] = stop, color

            message = {"color": self._gradient[n][1], "location": self._gradient[n][0]}
            messenger.publish("color_changed", message)

            self.gradientChanged.emit()
            self.update()

    def chooseColorAtPosition(self, n, current_color=None):
        dlg = QtWidgets.QColorDialog(self)
        if current_color:
            dlg.setCurrentColor(QtGui.QColor(current_color))

        if dlg.exec_():
            self.setColorAtPosition(n, dlg.currentColor().name())

    def choose_interior_color(self):
        dlg = QtWidgets.QColorDialog(self)
        dlg.setCurrentColor(QtGui.QColor(self.interior_color))

        if dlg.exec_():
            self.interior_color = dlg.currentColor().name()
            message = {"color": self.interior_color}
            messenger.publish("interior_color_changed", message)

    def _find_stop_handle_for_event(self, e):
        width = self.right_end - self.left_start
        midpoint = self._handle_h

        # Are we inside a stop point? First check y.
        if (
            e.y() >= midpoint - self._handle_h and
            e.y() <= midpoint + self._handle_h
        ):
            for n, (stop, color) in enumerate(self._gradient):
                translated_stop = stop * width + self.left_start
                if (
                    e.x() >= translated_stop - self._handle_w and
                    e.x() <= translated_stop + self._handle_w
                ):
                    return n

    def pressed_interior_color(self, e):
        height = self.height()
        width = height * 0.5
        rect = [
            self.width() - self.right_padding / 2 - width / 2,
            height * 0.25 - 14,
            width,
            width
        ]
        if (e.y() >= rect[1] and e.y() <= rect[1] + rect[3]):
            if (e.x() >= rect[0] and e.x() <= rect[0] + rect[2]):
                return True
        return False

    def mousePressEvent(self, e):
        # We're in this stop point.
        if e.button() == Qt.RightButton:
            n = self._find_stop_handle_for_event(e)
            if n is not None:
                _, color = self._gradient[n]
                self.chooseColorAtPosition(n, color)

        elif e.button() == Qt.LeftButton:
            if self.pressed_interior_color(e):
                self.choose_interior_color()
            else:
                n = self._find_stop_handle_for_event(e)
                if n is not None:
                    # Activate drag mode.
                    self._old_position = self._gradient[n][0]
                    self._drag_position = n


    def mouseReleaseEvent(self, e):
        if self._drag_position != None:
            message = {"color": self._gradient[self._drag_position][1],
                          "location": self._gradient[self._drag_position][0],
                          "old_location" : self._old_position}
            messenger.publish("color_moved", message)

        self._drag_position = None
        self._sort_gradient()

    def mouseMoveEvent(self, e):
        # If drag active, move the stop.
        if self._drag_position != None:
            width = self.right_end - self.left_start
            translated_x = e.x() - self.left_start
            stop = translated_x / width
            _, color = self._gradient[self._drag_position]
            self._gradient[self._drag_position] = stop, color
            self._constrain_gradient()
            self.update()

    def mouseDoubleClickEvent(self, e):
        # Calculate the position of the click relative 0..1 to the width.
        n = self._find_stop_handle_for_event(e)
        if n != None:
            self._sort_gradient() # Ensure ordered.
            # Delete existing, if not at the ends.
            if len(self._gradient) > 1:
                self.removeStopAtPosition(n)

        elif e.x() < self.right_end:
            width = self.right_end - self.left_start
            translated_x = e.x() - self.left_start
            stop = translated_x / width
            self.addStop(stop)
        elif self.pressed_interior_color(e):
            self.choose_interior_color()





