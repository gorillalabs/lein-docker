# lein-docker

A Leiningen plugin to build and deploy [Docker](https://www.docker.com/) images.

[![Clojars Project](https://img.shields.io/clojars/v/gorillalabs/lein-docker.svg)](http://clojars.org/gorillalabs/lein-docker)

## Usage

Add docker-deploy to your plugin list in your `project.clj`:

```clojure
:plugins [[gorillalabs/lein-docker "1.6.0"]]
```

(see version badge above for newest release)

Available commands:

    $ lein docker build [image-name [additional-arguments ... ]]
    $ lein docker push [image-name]
    $ lein docker rmi [image-name]

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

### Injecting environment variables into configuration

With a CI/CD pipeline such as Jenkins, you may want to generate Docker artifacts tagged
against specific properties of a build run. The values in the :docker configuration options
may contain bash-like references to environment variables, which will be replaced with
values from the environment.  For example:

```clojure
:docker {:image-name "myregistry.example.org/myimage"
         :tags ["%s-${BUILD_NUMBER:-unknown}"]}
```

Running `lein docker build` command under Jenkins line will generate an image tagged with the
project number and build number (e.g., 1.6.0-306).  If BUILD_NUMBER is undefined, as when
outside of Jenkins, then the default value ("unknown") will be spliced in instead.  If the
environment variable expression does not contain a default string and the referenced
environment variable is not defined, the expression is replaced with the empty string.

Environment variable injection is supported for the `:image-name`, `:tags`, `:dockerfile`,
and `:build-dir` entries in the project's `:docker` map.

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

