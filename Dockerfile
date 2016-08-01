FROM ubuntu:xenial
RUN apt-get update

###
# Java
###

# Install Java 7
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN apt-get install -y software-properties-common
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN apt-get install -y oracle-java7-installer
RUN rm -rf /var/lib/apt/lists/*
RUN rm -rf /var/cache/oracle-jdk7-installer

ENV JAVA_HOME /usr/lib/jvm/java-7-oracle

# Install Maven
RUN add-apt-repository -y universe
RUN apt-get update
RUN apt-get install -y maven
RUN mkdir -p /root/.m2



###
# Ruby
###
RUN apt-get install -y build-essential patch ruby-full
RUN gem install bundler
ENV BUNDLE_PATH /bundler



###
# Phantom JS
###

# Libraries to install phantomjs
RUN apt-get install -y wget libfontconfig1 libfreetype6


# Set up Environment variables for PhantomJS
ENV PHANTOMJS_VERSION 1.9.8
ENV PHANTOMJS_DIR /phantomjs

# Download and untar PhantomJS
RUN wget -q --continue -P $PHANTOMJS_DIR "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-${PHANTOMJS_VERSION}-linux-x86_64.tar.bz2"
RUN tar -xaf $PHANTOMJS_DIR/phantomjs* --strip-components=1 --directory "$PHANTOMJS_DIR"

# Set the PATH to include PhantomJS
ENV PATH $PHANTOMJS_DIR/bin:$PATH



###
# Firefox
###

# We need wget to download the custom version of Firefox, xvfb to have a virtual screen and Firefox so all necessary libraries are installed.
RUN apt-get install -y build-essential wget firefox


# Setting the Firefox version and installation directory through environment variables.
ENV FIREFOX_VERSION 38.0
ENV FIREFOX_DIR $HOME/firefox
ENV FIREFOX_FILENAME $FIREFOX_DIR/firefox.tar.bz2

# Create the Firefox directory, download the custom Firefox version from Mozilla and untar it.
RUN mkdir $FIREFOX_DIR
RUN wget -q --continue --output-document $FIREFOX_FILENAME "https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/${FIREFOX_VERSION}/linux-x86_64/en-US/firefox-${FIREFOX_VERSION}.tar.bz2"
RUN tar -xaf "$FIREFOX_FILENAME" --strip-components=1 --directory "$FIREFOX_DIR"

# Setting the PATH so our customer Firefox version will be used first
ENV PATH $FIREFOX_DIR:$PATH



###
# Xvfb for headless browser testing
###
RUN apt-get install -y xvfb dbus-x11
# Script using container should first run the following command:
# export DISPLAY=:99 && Xvfb :99 &


###
# Add our code
###

ADD . /code
WORKDIR /code
