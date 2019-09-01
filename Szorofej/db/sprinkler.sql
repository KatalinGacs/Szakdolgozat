CREATE TABLE IF NOT EXISTS Sprinklergroup (
name TEXT NOT NULL,
PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS Sprinklertype (
name TEXT NOT NULL,
minradius REAL NOT NULL,
maxradius REAL NOT NULL,
minangle REAL NOT NULL,
maxangle REAL NOT NULL,
fixwaterconsumption INTEGER NOT NULL,
waterconsumption REAL NOT NULL,
minpressure REAL NOT NULL,
sprinklergroup TEXT NOT NULL,
PRIMARY KEY (name),
FOREIGN KEY (sprinklergroup) REFERENCES Sprinklergroup (name)
);

CREATE TABLE IF NOT EXISTS Material (
name TEXT NOT NULL,
PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS Pipematerial (
radius REAL NOT NULL,
materialname TEXT NOT NULL,
PRIMARY KEY (radius),
FOREIGN KEY (materialname) REFERENCES Material (name)
);

CREATE TABLE IF NOT EXISTS Solenoidvalvematerial (
materialname TEXT NOT NULL,
PRIMARY KEY (materialname),
FOREIGN KEY (materialname) REFERENCES Material (name)
);

CREATE TABLE IF NOT EXISTS Sprinklermaterial (
sprinklertype TEXT NOT NULL,
materialname TEXT NOT NULL,
quantity INTEGER NOT NULL,
PRIMARY KEY (sprinklertype, materialname),
FOREIGN KEY (sprinklertype) REFERENCES Sprinklertype (name),
FOREIGN KEY (materialname) REFERENCES Material (name)
);