const express = require("express");
const router = express.Router();
const path = require("path");
const reader = require("./server/reader");
const urls = reader.getURLs();
const weblog = require("./server/weblog");
const log = weblog.putLogs;

function IsAllowBrowser(req) {
  const userAgent = req.headers["user-agent"];
  const isChrome = userAgent.includes("Chrome");
  const isChromeMobile =
    userAgent.includes("CriOS") || userAgent.includes("mobileApp");
  const IsHeartBeat = userAgent.includes("ELB-HealthChecker/2.0");
  const isEdge = userAgent.includes("Edge");

  return (isChrome || isChromeMobile || IsHeartBeat) && !isEdge;
}

function IsMobileBrowser(req) {
  const userAgent = req.headers["user-agent"];
  const isChromeMobile =
    userAgent.includes("Mobile") ||
    userAgent.includes("CriOS") ||
    userAgent.includes("mobileApp");

  return isChromeMobile;
}

router.get("/service", function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      res.redirect("/m/qrcode");
    } else {
      res.sendFile(path.join(__dirname, "/dist/service/index.html"));
    }
  } else {
    res.redirect("/support");
    return;
  }
});

router.get("/service/*", function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      res.redirect("/m/qrcode");
    } else {
      res.sendFile(path.join(__dirname, "/dist/service/index.html"));
    }
  } else {
    res.redirect("/support");
    return;
  }
});

router.post("/urls", function(req, res) {
  res.header("Content-Type", "application/json");
  res.send(urls);
});

router.get("/*", function(req, res) {
  res.redirect("/service");
});

module.exports = router;
