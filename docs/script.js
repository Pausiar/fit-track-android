(function () {
  "use strict";

  var RELEASE_URL = "./release.json";

  function formatBytes(bytes) {
    if (!bytes || isNaN(bytes)) return "";
    var mb = bytes / 1000 / 1000;
    return mb.toFixed(1).replace(".", ",") + " MB";
  }

  function formatDate(iso) {
    if (!iso) return "";
    var parts = String(iso).split("-");
    if (parts.length !== 3) return iso;
    var months = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"];
    var month = months[parseInt(parts[1], 10) - 1];
    return parseInt(parts[2], 10) + " de " + month + " de " + parts[0];
  }

  function setText(selector, value) {
    if (!value) return;
    document.querySelectorAll(selector).forEach(function (node) {
      node.textContent = value;
    });
  }

  function applyRelease(release) {
    if (!release) return;

    var file = release.fileName || "fit-track.apk";
    var url = release.url || "./downloads/" + file;
    var size = formatBytes(release.sizeBytes);

    document.querySelectorAll("[data-download]").forEach(function (link) {
      link.setAttribute("href", url);
      link.setAttribute("download", file);
    });

    setText("[data-release-version]", "v" + release.version);
    setText("[data-release-version-plain]", release.version);
    setText("[data-release-code]", String(release.versionCode));
    setText("[data-release-size]", size);
    setText("[data-release-date]", formatDate(release.publishedAt));
    setText("[data-release-android]", release.minAndroid ? release.minAndroid + "+" : "");
    setText("[data-release-sha]", release.sha256);
    setText("[data-release-fingerprint]", release.signingCertificateSha256);
    setText("[data-release-file]", file);

    document.querySelectorAll("[data-release-changelog]").forEach(function (list) {
      if (!Array.isArray(release.changelog)) return;
      list.textContent = "";
      release.changelog.forEach(function (item) {
        var li = document.createElement("li");
        li.className = "release-item";
        li.textContent = item;
        list.appendChild(li);
      });
    });

    var checksumLink = document.querySelector("[data-checksum-link]");
    if (checksumLink) {
      checksumLink.setAttribute("href", url + ".sha256");
    }

    document.documentElement.setAttribute("data-release-ready", "true");
  }

  function loadRelease() {
    if (!window.fetch) return;
    fetch(RELEASE_URL, { cache: "no-cache" })
      .then(function (response) {
        if (!response.ok) throw new Error("release metadata " + response.status);
        return response.json();
      })
      .then(function (release) {
        applyRelease(release);
        checkDownload();
      })
      .catch(function () {
        /* The static values in the HTML remain as a safe fallback. */
        document.documentElement.setAttribute("data-release-ready", "fallback");
        checkDownload();
      });
  }

  function wireCopyButtons() {
    document.querySelectorAll("[data-copy-target]").forEach(function (button) {
      button.addEventListener("click", function () {
        var target = document.querySelector(button.getAttribute("data-copy-target"));
        if (!target) return;
        var text = (target.textContent || "").trim();

        function done() {
          var original = button.getAttribute("data-label") || "Copiar";
          button.textContent = "Copiado";
          button.classList.add("is-copied");
          window.setTimeout(function () {
            button.textContent = original;
            button.classList.remove("is-copied");
          }, 1800);
        }

        if (navigator.clipboard && window.isSecureContext) {
          navigator.clipboard.writeText(text).then(done).catch(fallback);
        } else {
          fallback();
        }

        function fallback() {
          var area = document.createElement("textarea");
          area.value = text;
          area.setAttribute("readonly", "");
          area.style.position = "fixed";
          area.style.opacity = "0";
          document.body.appendChild(area);
          area.select();
          try { document.execCommand("copy"); done(); } catch (error) { /* no-op */ }
          document.body.removeChild(area);
        }
      });
    });
  }

  function wireReveal() {
    var items = document.querySelectorAll(".reveal");
    if (!items.length) return;

    if (!("IntersectionObserver" in window)) {
      items.forEach(function (item) { item.classList.add("is-visible"); });
      return;
    }

    var observer = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.12 });

    items.forEach(function (item) { observer.observe(item); });
  }

  function wireYear() {
    document.querySelectorAll("[data-year]").forEach(function (node) {
      node.textContent = String(new Date().getFullYear());
    });
  }

  function checkDownload() {
    var status = document.querySelector("[data-download-status]");
    var statusText = document.querySelector("[data-download-status-text]");
    if (!status || !statusText || !window.fetch) return;
    var link = document.querySelector("[data-download]");
    if (!link) return;
    var url = link.getAttribute("href");
    if (!url) return;

    fetch(url, { method: "HEAD", cache: "no-cache" })
      .then(function (response) {
        if (response.ok) {
          status.classList.remove("is-offline");
          statusText.textContent = "Descarga verificada disponible";
        } else {
          throw new Error("not available");
        }
      })
      .catch(function () {
        status.classList.add("is-offline");
        statusText.textContent = "No se pudo comprobar la descarga; usa el enlace directo";
      });
  }

  document.addEventListener("DOMContentLoaded", function () {
    wireReveal();
    wireCopyButtons();
    wireYear();
    loadRelease();
  });
})();
