<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.9" tiledversion="1.9.0" name="platformer" tilewidth="64" tileheight="64" tilecount="98" columns="14">
 <image source="platformPack_tilesheet.png" width="896" height="448"/>
 <tile id="0" class="Block">
  <properties>
   <property name="collidable" type="bool" value="true"/>
   <property name="type" value="Grass"/>
  </properties>
 </tile>
 <tile id="3" class="Block">
  <properties>
   <property name="collidable" type="bool" value="true"/>
   <property name="type" value="Dirt"/>
  </properties>
 </tile>
 <tile id="46" class="Block">
  <properties>
   <property name="collidable" type="bool" value="true"/>
   <property name="type" value="WoodenBox"/>
  </properties>
 </tile>
 <tile id="50" class="Entity">
  <properties>
   <property name="type" value="io.github.hider.tilegame.entities.Collectible"/>
  </properties>
 </tile>
 <tile id="70" class="Entity">
  <properties>
   <property name="mass" type="float" value="1"/>
   <property name="type" value="io.github.hider.tilegame.entities.Spike"/>
  </properties>
  <objectgroup draworder="index" id="2">
   <object id="1" x="0" y="29.5" width="63" height="34.5"/>
  </objectgroup>
 </tile>
 <tile id="73" class="Entity">
  <properties>
   <property name="type" value="io.github.hider.tilegame.entities.EndProtector"/>
  </properties>
 </tile>
 <tile id="74" class="Block">
  <properties>
   <property name="collidable" type="bool" value="true"/>
   <property name="type" value="WoodenBox"/>
  </properties>
 </tile>
 <tile id="82" class="Entity">
  <properties>
   <property name="downAnimationId" type="int" value="11"/>
   <property name="type" value="io.github.hider.tilegame.entities.EndButton"/>
  </properties>
  <objectgroup draworder="index" id="2">
   <object id="1" x="3.41266" y="29.7028" width="57.1304" height="34.1266"/>
  </objectgroup>
 </tile>
 <tile id="83">
  <properties>
   <property name="animationId" type="int" value="11"/>
  </properties>
 </tile>
</tileset>
