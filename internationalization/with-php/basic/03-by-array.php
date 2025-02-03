<?php
  $lang = 'en';
  if ($lang == 'en') {
    $trans = [
      'title'   => 'Example',
      'header'  => 'HOME',
      'welcome' => 'HELLO AND WELCOME',
    ];
  } elseif ($lang == 'es') {
    $trans = [
      'title'   => '',
      'header'  => 'ejemplo',
      'welcome' => 'Hola y bienvenida',
    ];
  }
?>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title> <?php echo $trans['title'] ?> </title>
</head>

<body>
    <h1><?php echo $trans['header'] ?></h1>
    <P> <?php echo $trans['welcome'] ?> </P>
</body>

</html>