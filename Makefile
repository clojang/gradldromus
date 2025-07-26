.PHONY: build clean test install

default: build

build:
	./gradlew build

clean:
	./gradlew clean
	rm -rf ~/.gradle/caches/modules-2/files-2.1/io.github.clojang/gradldromus/
	rm -rf ~/.gradle/caches/modules-2/metadata-2.*/descriptors/io.github.clojang/gradldromus

lint:
	./gradlew checkstyleMain

test:
	./gradlew test

fresh-test:
	./gradlew test --rerun-tasks

freshest-test:
	./gradlew clean test --refresh-dependencies --rerun-tasks

install: clean build
	./gradlew publishToMavenLocal