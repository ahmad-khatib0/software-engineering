<?php
  require 'vendor/autoload.php';

  require "includes/i18n_init.php";
  // putenv("LANG=$locale");
  // putenv("LANGUAGE=$locale"); //these tow lines if gettext didn't work

  // PhpMyAdmin\MoTranslator\Loader::loadFunctions();
  // setLocale(LC_ALL, $locale); //SET locale that php will use
  // _setLocale(LC_ALL, $locale);
  $domain = "messages"; //this called translation domain

  // textdomain($domain); //this optional, because it's = messages by default
  // _textdomain($domain);

  // bindtextdomain($domain, 'locales'); //tell gettext where translation messages are
  // _bindtextdomain($domain, 'locales');

  // bind_textdomain_codeset($domain, 'UTF-8');

  // _bind_textdomain_codeset($domain, 'UTF-8');

  $name  = "Dave";
  $count = 1;
  $pi    = 3.144553;
  setlocale(LC_ALL, $locale);
  // setlocale(LC_ALL, "es_ES.UTF-8");linux

  $formatter = new NumberFormatter($locale, NumberFormatter::DECIMAL);
  $formatter->setAttribute(NumberFormatter::DECIMAL, 5); //DEFAULT IS 3

  $timestamp = strtotime('20 JULY 1965');

  $date_formatter = new IntlDateFormatter($locale, 1, 1);
  $date_formatter->setPattern("EEEE, d MMMM Y");

  $fileName = "content/body.$locale.md";
  if (is_readable($fileName)) {
    $parser  = new Parsedown;
    $content = file_get_contents($fileName);
    $content = $parser->text($content);
  } else {
    $content = "Content for $locale , not found ";
  }

  $connection   = new PDO('mysql:host=localhost;dbname=phpi18n;charset=utf8', 'root', "");
  $sql          = "SELECT title_$locale AS title , description_$locale AS description , size FROM product";
  $sql_localize = "SELECT title  , description , size FROM product
  JOIN product_localize ON product.id = product_localize.product_id
  WHERE locale = :locale ";
  $stmt          = $connection->query($sql);
  $stmt_localize = $connection->prepare($sql_localize);
  $stmt_localize->bindValue(":locale", $locale, PDO::PARAM_STR);
  $stmt_localize->execute();
  $products          = $stmt->fetchAll();
  $products_localize = $stmt_localize->fetchAll();
?>

<!DOCTYPE html>
<html lang="<?php str_replace("_", "-", $locale);?>">

<head>
    <meta charset="UTF-8">
    <!-- <title> //=gettext("Example") </title> -->
    <!-- <title>  //=__("Example") </title> -->
    <title> <?=$translator->gettext("Example")?> </title>
</head>

<body>
    <?php require 'includes/lang_nav.php';?>
    <!-- <h1> //_('Home') </h1> -->
    <!-- <h1> __('Home') </h1> -->
    <h1><?=$translator->gettext('Home')?></h1>

    <!-- <P> _('HELLO AND WELCOME') </P> -->
    <!-- <P> __('HELLO AND WELCOME') </P>  -->
    <P> <?=$translator->gettext('HELLO AND WELCOME')?> </P>

    <!-- <P __('Thank You') </P> -->
    <!-- <P> $translator->gettext('Thank You')?> </P> -->
    <P> <?=$translator->gettext('thank-you')?> </P>

    <p> <?=sprintf($translator->gettext("Welcome, %s"), $name)?> </p>

    <p> <?=sprintf($translator->ngettext("You have %d message", "You have %d messages", $count), $count)?> </p>
    <!-- ngettext will decide wither to use plural or singular, you need to include  -->
    <!-- the singular and plural message in poedit -->

    <p> <?=$pi?> </p>
    <!-- ABOVE first solution, bellow second solution  -->
    <p> <?=$formatter->format($pi)?> </p>

    <p> <?=strftime("%A, %d %B %Y", $timestamp);?> </p>

    <p> <?php $date_formatter->format($timestamp)?> </p>

    <?php echo $content ?>

    <p>----------------------------- for the same table </p>
    <table>
        <thead>
            <th><?=$translator->gettext('Title')?></th>
            <th><?=$translator->gettext('Description')?></th>
            <th><?=$translator->gettext('Size')?></th>
        </thead>
        <tbody>
            <?php foreach ($products as $product): ?>
            <tr>
                <td> <?=$product['title']?> </td>
                <td> <?=$product['description']?> </td>
                <td> <?=$product['size']?> </td>
            </tr>

            <?php endforeach;?>
        </tbody>
    </table>
    <p>----------------------------- with table localize (joint on another translation table) </p>
    <table>
        <thead>
            <th><?=$translator->gettext('Title')?></th>
            <th><?=$translator->gettext('Description')?></th>
            <th><?=$translator->gettext('Size')?></th>
        </thead>
        <tbody>
            <?php foreach ($products_localize as $product): ?>
            <tr>
                <td> <?=$product['title']?> </td>
                <td> <?=$product['description']?> </td>
                <td> <?=$product['size']?> </td>
            </tr>

            <?php endforeach;?>
        </tbody>
    </table>
</body>

</html>