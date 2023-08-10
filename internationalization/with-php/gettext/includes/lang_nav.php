<nav>
    <?php foreach ($link_data as $link): ?>
    <?php if ($link['is_current']): ?>
    <?=$link['label']?>
    <?php else: ?>
    <a href="<?php echo $link['url'] ?>"><?=$link['label']?></a>
    <?php endif;?>
    <?php endforeach;?>
</nav>