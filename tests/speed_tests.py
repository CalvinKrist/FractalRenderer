import fractal
import inspect
from time import process_time
import sys
from datetime import datetime


def verify(width, height, value):
    return True


def print_results(total_results):
    for layer_name, results in total_results.items():
        print(layer_name)
        for item, result in results.items():
            print("\t(" + str(item[0]) + "," + str(item[1]) + "): \t" + str(result[0]))


output = print_results
method = None


def save_results(total_results):
    # Build the table headers
    table = "(w,h) | "
    for layer_name in total_results.keys():
        table += layer_name + " | "
    table += "\n--- |"

    for _ in total_results.keys():
        table += ":---:|"
    table += "\n"

    # Fill in table results
    if len(total_results.keys()) > 0:
        num_results = len(total_results[list(total_results.keys())[0]].keys())
        key_list = list(total_results[list(total_results.keys())[0]].keys())
        key_list.sort()

        for i in range(num_results):
            table += "(" + str(key_list[i][0]) + "," + str(key_list[i][1]) + ") | "
            for layer_name, results in total_results.items():
                table += str(results[key_list[i]][0]) + " |"
            table += "\n"

    # Build filename where we store the results
    now = datetime.now()
    dt_string = now.strftime("%d-%m-%Y_%H-%M-%S")
    result_file_name = "speed_results"
    if method is "c":
        result_file_name += "/c"
    elif method is "rust":
        result_file_name += "/rust"
    result_file_name += "/" + dt_string + ".md"

    # Write file results
    with open(result_file_name, "w") as fd:
        fd.write(table)

    # Update main results file
    text = ""
    with open("speed_results/README.md", "r") as fd:
        text = fd.read()
        if method is "c":
            text = text.replace("C\n", "C\n* [" + dt_string + "](c/" + dt_string + ".md)\n")
        if method is "rust":
            text = text.replace("Rust\n", "Rust\n* [" + dt_string + "](rust/" + dt_string + ".md)\n")
    with open("speed_results/README.md", "w") as fd:
        fd.write(text)

def test_by_size(layer, width, height, num=10):
    frac = fractal.Fractal()
    frac.width = width
    frac.height = height

    frac.insert_layer(0, layer)

    times = []
    result = None

    for i in range(num):
        start = process_time()
        result = frac.render()
        end = process_time()
        times.append(end - start)

    average = sum(times) / len(times)
    return average, verify(width, height, result)


def main():
    layers = ["SimpleBandsLayer", "SmoothBandsLayer", "HistogramLayer"]
    settings = [(800, 650, 100), (2000, 2000, 50), (5000, 5000, 15), (10000, 10000, 5)]

    results = {}

    classes_in_fractal = [elem[0] for elem in inspect.getmembers(fractal)]
    for layer in layers:
        if layer not in classes_in_fractal:
            results[layer] = "None"
        else:
            results[layer] = {}
            for setting in settings:
                width = setting[0]
                height = setting[1]
                iters = setting[2]
                layer_class_ = getattr(fractal, layer)
                layer_instance = layer_class_()
                print("Testing " + layer + " at " + str(setting))
                results[layer][setting] = test_by_size(layer_instance, width, height, iters)

    output(results)


def print_help():
    help = "-h: \t print help pages\n" \
           "--help:\t print help pages\n" \
           "-m: \t the module being used\n" \
           "\t\t\t c: \t using the C module\n" \
           "\t\t\t rust: \t using the Rust module\n" \
           "-o: \t set output destination (optional)\n" \
           "\t\t\t markdown: output to a file and print in the markdown file"
    print(help)


if __name__ == '__main__':
    if len(sys.argv) == 1:
        print_help()
    elif sys.argv[1] == "-h"or sys.argv[1] == "--help":
        print_help()
    else:
        if len(sys.argv) == 5 and sys.argv[1] == "-o" and sys.argv[2] == "markdown":
            output = save_results
        elif len(sys.argv) == 5 and sys.argv[3] == "-o" and sys.argv[4] == "markdown":
            output = save_results
        if len(sys.argv) == 3 and sys.argv[1] == "-m" and sys.argv[2] == "c":
            method = "c"
        elif len(sys.argv) == 5 and sys.argv[3] == "-m" and sys.argv[4] == "c":
            method = "c"
        elif len(sys.argv) == 5 and sys.argv[1] == "-m" and sys.argv[2] == "c":
            method = "c"
        elif len(sys.argv) == 3 and sys.argv[1] == "-m" and sys.argv[2] == "rust":
            method = "rust"
        elif len(sys.argv) == 5 and sys.argv[3] == "-m" and sys.argv[4] == "rust":
            method = "rust"
        elif len(sys.argv) == 5 and sys.argv[1] == "-m" and sys.argv[2] == "rust":
            method = "rust"
        if method == None:
            print("Must supply a method.")
        else:
            main()