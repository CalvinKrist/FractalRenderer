import sys
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import QStyle, QStyleOptionSlider, QMainWindow, QLabel, QVBoxLayout
from PyQt5.QtCore import Qt, QPoint, QRect
from PyQt5.QtGui import QPainter, QFont
from PyQt5.QtCore import pyqtSignal as Signal
from Messenger import messenger


def frange(min, max, interval):
    vals = []
    cur = min
    while cur < max:
        vals.append(cur)
        cur += interval
    return vals

class LabeledSlider(QtWidgets.QWidget):
    def __init__(self, minimum, maximum, interval=1, orientation=Qt.Horizontal,
            labels=None, parent=None):
        super(LabeledSlider, self).__init__(parent=parent)

        levels=frange(minimum, maximum+interval, interval)
        if labels:
            if not isinstance(labels, (tuple, list)):
                raise Exception("<labels> is a list or tuple.")
            if len(labels) != len(levels):
                raise Exception("Size of <labels> doesn't match levels.")
            self.levels=list(zip(levels,labels))
        else:
            self.levels=list(zip(levels,map(str,levels)))

        if orientation==Qt.Horizontal:
            self.layout=QtWidgets.QVBoxLayout(self)
        elif orientation==Qt.Vertical:
            self.layout=QtWidgets.QHBoxLayout(self)
        else:
            raise Exception("<orientation> wrong.")

        # gives some space to print labels
        self.left_margin=10
        self.top_margin=10
        self.right_margin=10
        self.bottom_margin=10

        self.layout.setContentsMargins(self.left_margin,self.top_margin,
                self.right_margin,self.bottom_margin)

        self.sl=QtWidgets.QSlider(orientation, self)
        self.sl.setMinimum(minimum)
        self.sl.setMaximum(maximum)
        self.sl.setValue(minimum)
        if orientation==Qt.Horizontal:
            self.sl.setTickPosition(QtWidgets.QSlider.TicksBelow)
            self.sl.setMinimumWidth(300) # just to make it easier to read
        else:
            self.sl.setTickPosition(QtWidgets.QSlider.TicksLeft)
            self.sl.setMinimumHeight(300) # just to make it easier to read
        self.sl.setTickInterval(interval)
        self.sl.setSingleStep(1)

        self.layout.addWidget(self.sl)

    def paintEvent(self, e):

        super(LabeledSlider,self).paintEvent(e)

        style=self.sl.style()
        painter=QPainter(self)
        st_slider=QStyleOptionSlider()
        st_slider.initFrom(self.sl)
        st_slider.orientation=self.sl.orientation()

        length=style.pixelMetric(QStyle.PM_SliderLength, st_slider, self.sl)
        available=style.pixelMetric(QStyle.PM_SliderSpaceAvailable, st_slider, self.sl)

        for v, v_str in self.levels:

            # get the size of the label
            rect=painter.drawText(QRect(), Qt.TextDontPrint, v_str)

            if self.sl.orientation()==Qt.Horizontal:
                # I assume the offset is half the length of slider, therefore
                # + length//2
                x_loc=QStyle.sliderPositionFromValue(self.sl.minimum(),
                        self.sl.maximum(), v, available)+length//2

                # left bound of the text = center - half of text width + L_margin
                left=x_loc-rect.width()//2+self.left_margin
                bottom=self.rect().bottom()

                # enlarge margins if clipping
                if v==self.sl.minimum():
                    if left<=0:
                        self.left_margin=rect.width()//2-x_loc
                    if self.bottom_margin<=rect.height():
                        self.bottom_margin=rect.height()

                    self.layout.setContentsMargins(self.left_margin,
                            self.top_margin, self.right_margin,
                            self.bottom_margin)

                if v==self.sl.maximum() and rect.width()//2>=self.right_margin:
                    self.right_margin=rect.width()//2
                    self.layout.setContentsMargins(self.left_margin,
                            self.top_margin, self.right_margin,
                            self.bottom_margin)

            else:
                y_loc=QStyle.sliderPositionFromValue(self.sl.minimum(),
                        self.sl.maximum(), v, available, upsideDown=True)

                bottom=y_loc+length//2+rect.height()//2+self.top_margin-3
                # there is a 3 px offset that I can't attribute to any metric

                left=self.left_margin-rect.width()
                if left<=0:
                    self.left_margin=rect.width()+2
                    self.layout.setContentsMargins(self.left_margin,
                            self.top_margin, self.right_margin,
                            self.bottom_margin)

            pos=QPoint(left, bottom)
            painter.drawText(pos, v_str)

        print(self.sl.value())
        return


class CentralOpacityChooserWidget(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()

        self.initUI()

    def initUI(self):
        self.font = QFont()
        self.font.setFamily("Arial")
        self.font.setPointSize(14)

        layout = QVBoxLayout()
        self.setLayout(layout)
        layout.setContentsMargins(0, 0, 0, 0)

        self.setStyleSheet('background-color:white')

        # Add layer selection tool
        layer_type_label = QLabel("Choose Opacity")
        layer_type_label.setFont(self.font)
        layout.addWidget(layer_type_label)

        # Add layer selection combo box
        selector = LabeledSlider(0, 100, 10)
        selector_font = QFont()
        selector_font.setFamily("Arial")
        selector_font.setPointSize(10)
        selector.setFont(selector_font)
        layout.addWidget(selector)


class OpacitySliderWindow(QMainWindow):
    def __init__(self, parent=None):
        super(OpacitySliderWindow, self).__init__(parent)

        central_widget = CentralOpacityChooserWidget()
        self.setStyleSheet('background-color:white')

        self.setCentralWidget(central_widget)


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

        self._opacity = [(0.0, 1, OpacitySliderWindow()), (1.0, 1, OpacitySliderWindow())]

        # Stop point handle sizes.
        self._handle_w = 10
        self._handle_h = 15

        self.left_start = self._handle_w / 2
        self.right_padding = 80
        self.right_end = self.width() - self.right_padding

        self._drag_position = None
        self._opacity_drag_position = None
        self._old_position = None
        self._opacity_old_position = None

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

        rect = QtCore.QRect(self.left_start, height * 0.25, width, height * 0.5)
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

        # Draw the stop opacity handles.
        for stop, color, _ in self._opacity:
            painter.setBrush(QtGui.QBrush(QtGui.QColor(color), Qt.SolidPattern))

            translated_stop = stop * width + self.left_start
            rect = QtCore.QRect(
                translated_stop - self._handle_w / 2,
                height - self._handle_h * 2 / 3,
                self._handle_w,
                self._handle_h * 2 / 3
            )
            painter.drawRect(rect)

            points = [
                QtCore.QPoint(translated_stop - self._handle_w / 2, height - self._handle_h * 2 / 3),
                QtCore.QPoint(translated_stop + self._handle_w / 2, height - self._handle_h * 2 / 3),
                QtCore.QPoint(translated_stop, height - self._handle_h)

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

    def _sort_opacity(self):
        self._opacity = sorted(self._opacity, key=lambda g:g[0])

    def _constrain_gradient(self):
        self._gradient = [
            # Ensure values within valid range.
            (max(0.0, min(1.0, stop)), color)
            for stop, color in self._gradient
        ]

    def _constrain_opacity(self):
        self._opacity = [
            # Ensure values within valid range.
            (max(0.0, min(1.0, stop)), opacity)
            for stop, opacity, _ in self._opacity
        ]

    def gradient(self):
        return self._gradient

    def opacity(self):
        return self._opacity

    def add_gradient_stop(self, stop, color=None):
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

    def add_opacity_stop(self, stop):
        # Stop is a value 0...1, find the point to insert this stop
        # in the list.
        assert 0.0 <= stop <= 1.0

        index = -1
        for n, g in enumerate(self._opacity):
            if g[0] > stop:
                # Insert before this entry, with specified or next color.
                self._opacity.insert(n, (stop, 1.0, OpacitySliderWindow()))
                index = n
                break
        if index == -1:
            # Insert at end of list
            index = len(self._gradient)
            self._opacity.append((stop, 1.0, OpacitySliderWindow()))

        message = {"opacity" : self._opacity[index][1], "location": self._opacity[index][0]}
        messenger.publish("opacity_added", message)

        self.update()

    def remove_gradient_stop_at_position(self, n):
        if len(self._gradient) > 1:
            message = {"color": self._gradient[n][1], "location": self._gradient[n][0]}
            messenger.publish("color_removed", message)

        del self._gradient[n]
        self.gradientChanged.emit()
        self.update()

    def remove_opacity_stop_at_position(self, n):
        if len(self._opacity) > 1:
            message = {"opacity": self._opacity[n][1], "location": self._opacity[n][0]}
            messenger.publish("opacity_removed", message)

        del self._opacity[n]
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

    def chooseOpacityAtPosition(self, n, current_opacity=None):
        self._opacity[n][2].show()

    def choose_interior_color(self):
        dlg = QtWidgets.QColorDialog(self)
        dlg.setCurrentColor(QtGui.QColor(self.interior_color))

        if dlg.exec_():
            self.interior_color = dlg.currentColor().name()
            message = {"color": self.interior_color}
            messenger.publish("interior_color_changed", message)

    def _find_gradient_stop_handle_for_event(self, e):
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

    def _find_opacity_stop_handle_for_event(self, e):
        width = self.right_end - self.left_start
        midpoint = self.height() - self._handle_h

        # Are we inside a stop point? First check y.
        if (
            e.y() >= midpoint - self._handle_h and
            e.y() <= midpoint + self._handle_h
        ):
            for n, (stop, opacity, win) in enumerate(self._opacity):
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
            n = self._find_gradient_stop_handle_for_event(e)
            m = self._find_opacity_stop_handle_for_event(e)
            if n:
                _, color = self._gradient[n]
                self.chooseColorAtPosition(n, color)
            if m:
                _, opacity, _ = self._opacity[m]
                self.chooseOpacityAtPosition(m, opacity)

        elif e.button() == Qt.LeftButton:
            if self.pressed_interior_color(e):
                self.choose_interior_color()
            else:
                n = self._find_gradient_stop_handle_for_event(e)
                m = self._find_opacity_stop_handle_for_event(e)
                if n:
                    # Activate drag mode.
                    self._old_position = self._gradient[n][0]
                    self._drag_position = n
                elif m:
                    # Activate drag mode.
                    self._opacity_old_position = self._opacity[m][0]
                    self._opacity_drag_position = m

    def mouseReleaseEvent(self, e):
        if self._drag_position:
            message = {"color": self._gradient[self._drag_position][1],
                          "location": self._gradient[self._drag_position][0],
                          "old_location" : self._old_position}
            messenger.publish("color_moved", message)
        if self._opacity_drag_position:
            message = {"opacity": self._opacity[self._opacity_drag_position][1],
                          "location": self._opacity[self._opacity_drag_position][0],
                          "old_location" : self._opacity_old_position}
            messenger.publish("opacity_moved", message)

        self._drag_position = None
        self._opacity_drag_position = None
        self._sort_gradient()
        self._sort_opacity()

    def mouseMoveEvent(self, e):
        # If drag active, move the stop.
        if self._drag_position:
            width = self.right_end - self.left_start
            translated_x = e.x() - self.left_start
            stop = translated_x / width
            _, color = self._gradient[self._drag_position]
            self._gradient[self._drag_position] = stop, color
            self._constrain_gradient()
            self.update()
        elif self._opacity_drag_position:
            width = self.right_end - self.left_start
            translated_x = e.x() - self.left_start
            stop = translated_x / width
            _, opacity, win = self._opacity[self._opacity_drag_position]
            self._opacity[self._opacity_drag_position] = stop, opacity, win
            self._constrain_opacity()
            self.update()

    def mouseDoubleClickEvent(self, e):
        # Calculate the position of the click relative 0..1 to the width.
        n = self._find_gradient_stop_handle_for_event(e)
        m = self._find_opacity_stop_handle_for_event(e)
        if n:
            self._sort_gradient() # Ensure ordered.
            # Delete existing, if not at the ends.
            if len(self._gradient) > 1:
                self.remove_gradient_stop_at_position(n)
        elif m:
            self._sort_opacity()  # Ensure ordered.
            # Delete existing, if not at the ends.
            if len(self._opacity) > 1:
                self.remove_opacity_stop_at_position(m)
        elif e.x() < self.right_end:
            width = self.right_end - self.left_start
            translated_x = e.x() - self.left_start
            stop = translated_x / width

            if e.y() < self.height() / 2:
                self.add_gradient_stop(stop)
            else:
                self.add_opacity_stop(stop)
        elif self.pressed_interior_color(e):
            self.choose_interior_color()