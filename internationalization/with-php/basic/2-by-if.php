<?php
  $lang = 'es';
?>

<!DOCTYPE html>
<html>

<head>
    <title> Example </title>
</head>

<body>
    <h1>
        <?php if ($lang == 'en'): ?>
        HOME
        <?php elseif ($lang == 'es'): ?>
        CASA
        <?php endif?>
    </h1>
    <P> HELLO AND WELCOME </P>
</body>

</html>