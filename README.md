# lein-docker

A Leiningen plugin to build and deploy [Docker](https://www.docker.com/) images.

[![Clojars Project](https://img.shields.io/clojars/v/gorillalabs/lein-docker.svg)](http://clojars.org/gorillalabs/lein-docker)

## Usage

Add docker-deploy to your plugin list in your `project.clj`:

```clojure
:plugins [[gorillalabs/lein-docker "1.3.0"]]
```

(see version badge above for newest release)

Available commands:

    $ lein docker build
    $ lein docker push

## Configuration

You can add the following configuration options at the root of your `project.clj`:

```clojure
:docker {:image-name "myregistry.example.org/myimage"
         :tags ["%s" "latest"] ; %s will splice the project version into the tag
         :dockerfile "target/dist/Dockerfile"
         :build-dir  "target"}
```

Defaults:

* `:image-name` is your project's name (without the group ID)
* `:tags` is your project's version
* `:dockerfile` points to `Dockerfile`
* `:build-dir` points to the project's root

## Releasing your docker images

You can use Leiningen to handle your technical release process.
In order to do that with your Docker image instead of your plain jar file, configure your
release tasks similar to that:

```clojure
:release-tasks [["vcs" "assert-committed"]
                ["change" "version" "leiningen.release/bump-version" "release"]
                ["vcs" "commit"]
                ["vcs" "tag"]
                ["clean"]
                ["uberjar"]
                ["docker" "build"]
                ["docker" "push"]
                ["change" "version" "leiningen.release/bump-version"]
                ["vcs" "commit"]
                ["vcs" "push"]]
```

## License

[MIT License](LICENSE)

Copyright (c) 2015, Tobias Sarnowski
              2018, Dr. Christian Betz

