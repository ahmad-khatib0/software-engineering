<?php
$i18n                     = new App\I18n(['en_US', 'en_GB', 'en', 'es', "es_ES"]);
list($subdomain, $domain) = explode(".", $_SERVER['HTTP_HOST'], 2);

//   convert en_gb or en-gb from the browser to => en_GB what php needs
$locale = $i18n->getBestMatch($subdomain);

$getBrowserLanguages = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
// print($i18n->getAcceptLocales());
if ($locale == null) {
  $lang = $this->getLocaleForRedirect();
  // exit;
  $subdomain = substr($locale, 0, 2);
  header("Location: http://" . $subdomain . ".localhost:3000/");
  exit;
}

$translator = new PhpMyAdmin\MoTranslator\Translator("locales/$locale/LC_MESSAGES/messages.mo"); //or
// $translator = new PhpMyAdmin\MoTranslator\Translator("locales/$locale.mo");

$link_data = $i18n->getLinkData(['en_GB' => "English", 'es' => 'Espanol']);
setcookie("locale", $locale, time() + 60 * 60 * 24 * 15, '/', $domain);
//15 days
?>