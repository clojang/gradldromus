.PHONY: build clean test install version release

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

version:
	@./gradlew properties | grep '^version:' | cut -d' ' -f2

release:
	@version=$$(./gradlew properties | grep '^version:' | cut -d' ' -f2) && \
	git tag -a "v$$version" -m "Release version $$version" && \
	echo "Tagged release v$$version"

publish: clean build release just-publish

just-publish:
	@git pull origin main --rebase && \
	git push origin main && \
	git push origin main --tags