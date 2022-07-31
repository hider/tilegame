<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.9" tiledversion="1.9.0" name="player" tilewidth="96" tileheight="96" tilecount="8" columns="4">
 <image source="platformerPack_character.png" width="384" height="192"/>
 <tile id="0" class="Entity">
  <properties>
   <property name="downAnimationId" type="int" value="3"/>
   <property name="jumpingAnimationId" type="int" value="2"/>
   <property name="mass" type="float" value="46"/>
   <property name="type" value="io.github.hider.tilegame.entities.Player"/>
   <property name="walkingAnimationId" type="int" value="1"/>
  </properties>
  <objectgroup draworder="index" id="4">
   <object id="5" name="hitbox" x="27.5984" y="30" width="46.5056" height="66"/>
  </objectgroup>
 </tile>
 <tile id="1">
  <properties>
   <property name="animationId" type="int" value="2"/>
  </properties>
 </tile>
 <tile id="2" class="Entity">
  <properties>
   <property name="animationId" type="int" value="1"/>
   <property name="type" value="io.github.hider.tilegame.entities.Player"/>
  </properties>
  <objectgroup draworder="index" id="2">
   <object id="1" x="27.6" y="30" width="46.51" height="66"/>
  </objectgroup>
  <animation>
   <frame tileid="2" duration="200"/>
   <frame tileid="3" duration="200"/>
  </animation>
 </tile>
 <tile id="6">
  <properties>
   <property name="animationId" type="int" value="3"/>
  </properties>
 </tile>
</tileset>
