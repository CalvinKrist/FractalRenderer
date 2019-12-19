import unittest
import fractal

class HistogramLayerTester(unittest.TestCase):

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


class FractalTester(unittest.TestCase):

    def test_toString(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        desc = str(frac)
        self.assertEqual("fractal: \n\thistogramLayer\n", desc)

    def test_x(self):
        frac = fractal.Fractal()

        self.assertEqual(0, frac.x)
        frac.x = 1.11
        self.assertEqual(1.11, frac.x)

    def test_y(self):
        frac = fractal.Fractal()

        self.assertEqual(0, frac.y)
        frac.y = 1.11
        self.assertEqual(1.11, frac.y)

    def test_viewport_width(self):
        frac = fractal.Fractal()

        self.assertEqual(4, frac.viewport_width)
        frac.viewport_width = 1.11
        self.assertEqual(1.11, frac.viewport_width)

    def test_insert_layer(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        self.assertEqual(1, frac.layer_count())

    def test_get_layer_functionality(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        retrieved_layer = frac.get_layer(0)
        retrieved_layer.opacity = 0.2

        self.assertEqual(retrieved_layer.opacity, layer.opacity)

    def test_get_layer_memory_leak(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()
        frac.insert_layer(0, layer)

        retrieved_layer = frac.get_layer(0)

        self.assertEqual(retrieved_layer, layer)

    def test_get_layer_invalid(self):
        frac = fractal.Fractal()

        self.assertRaises(IndexError, frac.get_layer, 0)
        self.assertRaises(IndexError, frac.get_layer, -1)

    def test_remove_layer_functionality(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()

        # Insert then delete layer
        frac.insert_layer(0, layer)
        deleted_layer = frac.remove_layer(0)

        # Assert list length and equality of deleted layer
        self.assertEqual(0, frac.layer_count())
        deleted_layer.opacity = 0.2
        self.assertEqual(deleted_layer.opacity, layer.opacity)

    def test_remove_layer_memory_leak(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()

        # Insert then delete layer
        frac.insert_layer(0, layer)
        deleted_layer = frac.remove_layer(0)

        self.assertEqual(deleted_layer, layer)

    def test_remove_layer_invalid(self):
        frac = fractal.Fractal()

        self.assertRaises(IndexError, frac.remove_layer, 0)
        self.assertRaises(IndexError, frac.remove_layer, -1)

    def test_insert_layer_invalid(self):
        frac = fractal.Fractal()
        layer = fractal.HistogramLayer()

        self.assertRaises(IndexError, frac.insert_layer, 1, layer)
        self.assertRaises(IndexError, frac.insert_layer, -1, layer)

class Palette(unittest.TestCase):

    def test_toString(self):
        palette = fractal.Palette()
        self.assertEqual("palette string", str(palette))

    def test_interior_color(self):
        palette = fractal.Palette()
        self.assertEqual(palette.interior_color, (255, 255, 255))

        palette.interior_color = (10, 11, 12)
        self.assertEqual(palette.interior_color, (10, 11, 12))

    # TODO: fix with features
    def test_color_at_valid(self):
        palette = fractal.Palette()

        self.assertEqual(palette.color_at(0.5), (255, 255, 255))

    # TODO: fix with features
    def test_opacity_at_valid(self):
        palette = fractal.Palette()

        self.assertEqual(palette.opacity_at(0.5), 1.0)

    # TODO: fix with features
    def test_add_color_valid(self):
        palette = fractal.Palette()

        self.assertEqual(palette.add_color((255, 255, 255), 0.5), True)

    # TODO: fix with features
    def test_add_opacity_valid(self):
        palette = fractal.Palette()

        self.assertEqual(palette.add_opacity(1.0, 0.5), True)

    # TODO: fix with features
    def test_remove_color_valid(self):
        palette = fractal.Palette()

        self.assertEqual(palette.remove_color(0.5), True)

    # TODO: fix with features
    def test_remove_opacity_valid(self):
        palette = fractal.Palette()

        self.assertEqual(palette.remove_opacity(0.5), True)

if __name__ == '__main__':
    unittest.main()