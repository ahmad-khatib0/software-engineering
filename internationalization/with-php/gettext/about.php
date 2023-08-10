<?php
  require 'vendor/autoload.php';
  require "includes/i18n_init.php"
?>

<!DOCTYPE html>
<html lang="<?php str_replace("_", "-", $locale);?>">

<head>
    <meta charset="UTF-8">
    <title> <?=$translator->gettext("Example")?> </title>
</head>

<body>
    <?php require 'includes/lang_nav.php';?>
    <h1><?=$translator->gettext('About')?></h1>

</body>

</html>