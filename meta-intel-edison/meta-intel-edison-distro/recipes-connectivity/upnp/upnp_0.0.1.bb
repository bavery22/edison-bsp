DESCRIPTION="UPnP Service"
LICENSE = "MIT"

SRC_URI = "git://github.com/srware/iotrp-upnp-service.git;protocol=https"
SRCREV = "${AUTOREV}"

LIC_FILES_CHKSUM = "file://LICENSE;md5=ea398a763463b76b18da15f013c0c531"

S = "${WORKDIR}/git"

DEPENDS = "nodejs-native"

do_compile() {
    # changing the home directory to the working directory, the .npmrc will be created in this directory
    export HOME=${WORKDIR}

    # does not build dev packages
    npm config set dev false

    # access npm registry using http
    npm set strict-ssl false
    npm config set registry http://registry.npmjs.org/

    # configure http proxy if neccessary
    if [ -n "${http_proxy}" ]; then
        npm config set proxy ${http_proxy}
    fi
    if [ -n "${HTTP_PROXY}" ]; then
        npm config set proxy ${HTTP_PROXY}
    fi

    # configure cache to be in working directory
    npm set cache ${WORKDIR}/npm_cache

    # clear local cache prior to each compile
    npm cache clear

    # compile and install  node modules in source directory
    npm --arch=${TARGET_ARCH} --verbose install
}

do_install() {
   install -d ${D}${libdir}/upnp-service
   install -d ${D}/var/lib/upnp-service
   cp -r ${S}/node_modules ${D}${libdir}/upnp-service
   install -m 0644 ${S}/src/upnp-service.js ${D}${libdir}/upnp-service/upnp-service.js
   install -d ${D}${systemd_unitdir}/system/
   install -m 0644 ${S}/src/upnp.service ${D}${systemd_unitdir}/system/
}

inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN} = "upnp.service"

FILES_${PN} = "${libdir}/upnp-service \
               ${systemd_unitdir}/system \
               /var/lib/upnp-service \
               ${bindir}/"

PACKAGES = "${PN}"

