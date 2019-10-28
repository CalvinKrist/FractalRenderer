#pragma once

#include <map>
#include <string>

union VARIANT {
	int integer;
	double doub;
};

enum variant_type {
	INTEGER, DOUBLE
};

struct entry {
	union VARIANT data;
	enum variant_type type; 
};

class Parameters {
	public:
		Parameters();
		~Parameters();
	private:
		std::map<std::string, entry> parameters;
};