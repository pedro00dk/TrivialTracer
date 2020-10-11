build:
	mvn clean compile

run: build
	mvn exec:exec
