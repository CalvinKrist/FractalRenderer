import unittest
import fractal


class HistogramLayer(unittest.TestCase):

    def test_toString(self):
        layer = fractal.HistogramLayer()
        desc = str(layer)
        self.assertEqual("histogramLayer", desc)

    def test_opacity(self):
        layer = fractal.HistogramLayer()

        self.assertEqual(1, layer.opacity)
        layer.opacity = 0.2
        self.assertEqual(0.2, round(layer.opacity, 5))

    def test_name(self):
        layer = fractal.HistogramLayer()

        self.assertEqual("My Layer", layer.name)
        layer.name = "Layer 1"
        self.assertEqual("Layer 1", layer.name)

    def test_is_visible(self):
        layer = fractal.HistogramLayer()

        self.assertEqual(True, layer.is_visible)
        layer.is_visible = False
        self.assertEqual(False, layer.is_visible)

if __name__ == '__main__':
    unittest.main()
