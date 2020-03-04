FROM ubuntu:18.04

RUN apt-get update && apt-get install -y \
    build-essential \
    liblzma-dev \
    openjdk-11-jdk \
    patch \
    ruby \
    ruby-dev \
    zlib1g-dev \
 && gem update --system --no-document \
 && gem install bundler:2.1.4 --no-document \
 && rm -rf /var/lib/apt/lists/*

RUN apt-get update && apt-get install -y \
	apt-transport-https \
	ca-certificates \
	curl \
	gnupg \
	--no-install-recommends \
	&& curl -sSL https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
	&& echo "deb https://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
	&& apt-get update && apt-get install -y \
	google-chrome-stable \
	fontconfig \
	fonts-ipafont-gothic \
	fonts-wqy-zenhei \
	fonts-thai-tlwg \
	fonts-kacst \
	fonts-symbola \
	fonts-noto \
	fonts-freefont-ttf \
	--no-install-recommends \
	&& apt-get purge --auto-remove -y curl gnupg \
	&& rm -rf /var/lib/apt/lists/*