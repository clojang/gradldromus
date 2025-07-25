default: build

build:
	./gradlew build

clean:
	./gradlew clean

install: clean build
	./gradlew publishToMavenLocal