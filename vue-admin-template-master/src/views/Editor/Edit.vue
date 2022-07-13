<template>
  <div class="app-container">
    <div>
        <el-container>
           <!-- <el-header>Header</el-header> -->
           <el-container>
                <el-aside width="260px">
                       <Widget/>
                </el-aside>

                <el-main >
                    <el-scrollbar class="Scrollbar">
                        <div id="webgl" style="position:relative">
                        </div>
                    </el-scrollbar>
                </el-main>
                <el-aside width="260px">
                    <Attribute/>
                </el-aside>
            </el-container>
            <el-footer>
                <Footer/>
            </el-footer>
        </el-container>
      <!-- <el-row :gutter="20">
        <el-col :span="3" :xs="24">
          <el-card style="margin-bottom:20px;height: 500px;">

          </el-card>
        </el-col>
        <el-col :span="16" :xs="24">
            <div id="webgl" style="position:relative">

            </div>
        </el-col>
        <el-col :span="3" :xs="24">
          <el-card style="margin-bottom:20px;height: 500px;">

          </el-card>
        </el-col>
      </el-row> -->
    </div>


  </div>
</template>

<script>
import * as THREE from 'three/build/three.module.js'
import Stats from '../../store/Stats/stats.min.js'
import Widget from '../Editor/Widget/Widget.vue'
import Attribute from '../Editor/Attribute/index.vue'
import Footer from '../Editor/Footer/index.vue'
import {TransformControls} from 'three/examples/jsm/controls/TransformControls'
import {DragControls} from 'three/examples/jsm/controls/DragControls'
import {TrackballControls} from 'three/examples/jsm/controls/TrackballControls'
import {OrbitControls} from 'three/examples/jsm/controls/OrbitControls'
import { Color, Group, Object3D} from '../../lib/three.module.js'
import {MOUSE} from 'three/src/constants'
import {AxesHelper} from 'three/src/helpers/AxesHelper'
import {GridHelper} from 'three/src/helpers/GridHelper.js'
import {EventManager} from '../../lib/EventManager'
import {DirectionalLightHelper} from 'three/src/helpers/DirectionalLightHelper'
import { range } from "../../components/mode/range";
import { node } from "../../components/mode/node";
//初始数据
let renderer,camera,scene,geometry,material,mesh,stats,objs;
//  let container=document.getElementById('webgl');
   var controls={
            positionX:0,
            positionY:0,
            positionZ:0
        }
let cameraControls;
let transformControl;
let eventManager;
let directionalLight,ambientLight;
let id;
// let mouse;
// let raycaster;
/*这里记录一个小bug
当拖拽物体略过其他物体上时会造成物体全黑
应该是材质问题使用了法线变换颜色材质，换成正常颜色应该是没有问题

*/
let transing=false;
let axesHelper,gridHelper,directLightHelper;
let cacheobjects;
export default {
    name:'Editor',
    components:{
        Widget,
        Attribute,
        Footer
    },
    data() {
        return {
             state:{
                GridHelper:true,
                AxesHelper:true,
                LightHelper:true,
                PlaneHelper:false,
                BoxHelper:false,
                Control:true
            },
            configuration:{},
        }
    },
    watch:{
        'state.GridHelper':{
            // immediate:true,
            deep:true,
            handler(newValue){
                if(newValue){
                    this.AddGrid();
                }else{
                    this.RemoveGrid();
                }
            }
        },
         'state.AxesHelper':{
            // immediate:true,
            deep:true,
            handler(newValue){
                if(newValue){
                    this.AddAxes();
                }else{
                    this.RemoveAxes();
                }
            }
        },
        'state.LightHelper':{
            // immediate:true,
            deep:true,
            handler(newValue){
                if(newValue){
                    this.AddLight();
                }else{
                    this.RemoveLight();
                }
            }
        },
        'state.Control':{
            // immediate:true,
            deep:true,
            handler(newValue){
                if(newValue){
                    this.AddControl();
                }else{
                    this.RemoveControl();
                }
            }
        }
    },
    methods: {
        initstate(){
            this.state={
                GridHelper:true,
                AxesHelper:true,
                LightHelper:true,
                PlaneHelper:false,
                BoxHelper:false,
                Control:true
            };
        },
        AddModel(){
          this.$bus.$off('AddObjects');
          //防止多次触发bus总线
            this.$bus.$on('AddObjects',(em,item)=>{
                if(item.name=='基础几何模型'||item.name=='3D音乐模型'){
              this.AddElement(em.type);
          }
          else{
              this.LoadElement(em.type);
          }
            })
        },
        findcurrentObj(){
            let that=this;
            //重定向this,好利用总线
            this.$bus.$on('requestObj',(target)=>{
                let object=scene.getObjectByProperty('uuid',target.uuid);
                that.$bus.$emit('responseObj',object);
            });

        },
        CheckFromTree(){
            this.$bus.$on('checkEvent',(target)=>{
                if(this.state.Control==true){
                if(!target){
                    //处理对于传过来是空目标的情况，直接移除控件
                     transformControl.detach();
                    scene.remove(transformControl);
                }
                else {
                    if(transformControl.object&&target.uuid!=transformControl.object.uuid){
                    //如果从场景结构树中得到的物体与目前的不同才进行事件处理
                    //即更换射线拾取

                    transformControl.detach();
                    scene.remove(transformControl);
                    //先删除当前控件对象
                    scene.traverse(function(obj){
                        if(obj.uuid==target.uuid){
                            if(obj.type.indexOf('Helper')==-1){
                                //如果不是helper
                               /* 能否直接传uuid进来查然后传出去？

                               需要重构
                               */
                                scene.add(transformControl);
                                transformControl.attach(obj);


                            }
                              else{
                                  transformControl.detach();
                                 scene.remove(transformControl);


                              }
                             return;
                    }
                    });

                }
                else if(!transformControl.object){
                    //如果控件本身不存在那么直接加
                    scene.traverse(function(obj){
                        if(obj.uuid==target.uuid){
                            //同样需要判断是否是helper
                        if(obj.type.indexOf('Helper')==-1){
                                //如果不是helper

                                scene.add(transformControl);
                                transformControl.attach(obj);


                            }
                              else{
                                  transformControl.detach();
                                 scene.remove(transformControl);


                              }
                        return;
                    }
                    });
                }
                }
                }
            });
        },
        RemoveGrid(){
            scene.remove(gridHelper);
            //console.log(scene);
        },
        AddGrid(){
            scene.add(gridHelper);
        },
        RemoveAxes(){
            scene.remove(axesHelper);
        },
        AddAxes(){
            scene.add(axesHelper);
        },
        RemoveLight(){
            scene.remove(directLightHelper);
        },
        AddLight(){
            scene.add(directLightHelper);
        },
        RemoveControl(){
            transformControl.detach();
            scene.remove(transformControl);
            let object=null;
            this.$bus.$emit('getEvent',object);
        },
        AddControl(){
            scene.add(transformControl);

        },
      AddHelperAndControl(){
        this.AddAxes();
        this.AddControl();
        this.AddGrid();
        this.AddLight();
      },
      RemoveHelperAndControl(){
        this.RemoveGrid();
        this.RemoveAxes();
        this.RemoveLight();
        this.RemoveControl();
      },
        initHelper(){
            //加坐标轴辅助工具
            axesHelper=new AxesHelper(500);
            axesHelper.raycast=()=>{};
            axesHelper.name="坐标轴";
            scene.add(axesHelper);
            gridHelper=new GridHelper(500,40,'rgb(200,200,200)','rgb(100,100,100)');
            gridHelper.raycast=()=>{};
            gridHelper.name="网格板";
            scene.add(gridHelper);
            //将两个辅助工具raycast射线方法置空使得其禁用变换控件
            directLightHelper=new DirectionalLightHelper(directionalLight,7);
            //directLightHelper.raycast=()=>{};
            directLightHelper.name="定向光助手";
            directLightHelper.type="DirtectionalLightHelper";
            scene.add(directLightHelper);




        },
        initCameraControl(){
            cameraControls=new OrbitControls(camera,renderer.domElement);
            cameraControls.enableDamping=true;
            cameraControls.mouseButtons={
                LEFT:null,
                MIDDLE:MOUSE.DOLLY,
                RIGHT:MOUSE.ROTATE
                //左键不控制相机
                //右键控制旋转
                //中间键控制缩放
                //ctrl+右键控制相机平移
            }
        },
        getParents(obj){
          if(obj.parent){
            if(obj.parent.type=="Scene"){
              return obj;
            }else{
              while(obj.parent.type!="Scene"){
                obj=obj.parent;
                console.log(obj);
              }
              return obj;
            }
          }
        },

        initEventManager(){
            eventManager=new EventManager(renderer.domElement,scene,camera,objs);
            eventManager.addEventListener('mousemove',(event)=>{
                //全局canvas触发拿到物体指针队列
                if(event.intersection.length){
                    const object=event.intersection[0].object;

                    if(object==cacheobjects){
                        //如果当下鼠标还在同一个物体下不触发任何操作

                        return;
                    }
                    else if(object!=cacheobjects&&cacheobjects){
                        //如果当下鼠标从一个物体移动到另一个物体时
                        //先触发颜色恢复再触发变色

                        cacheobjects.material.color.multiplyScalar(0.5);
                        //返回原来颜色
                    }

                    //如果有材质
                    if(object.material){
                        object.material.color.multiplyScalar(2);
                        //变亮

                        cacheobjects=object;
                    }
                }else{
                    //如果鼠标移动到场景空白处恢复
                    if(cacheobjects){
                        cacheobjects.material.color.multiplyScalar(0.5);

                        //返回原来颜色
                    }
                    cacheobjects=null;
                }
            });
             // //物体拾取事件处理
            eventManager.addEventListener('click',(event)=>{

                if(transing){
                    transing=false;
                    console.log(transing);
                    return false;
                }
                if(event.intersection.length){
                    const object=event.intersection[0].object;
                    console.log(event.intersection);
                    if(object.type=="TransformControlsPlane"){
                        //如果选取到控制器本身直接跳过
                        transformControl.detach();
                        scene.remove(transformControl);
                    }
                    else{
                        //选取到物体
                          if(this.state.Control==true)
                          {
                              //只有当Control属性开启时才启用transformControl
                              //但不影响显示Tree当前点亮
                            scene.add(transformControl);

                            //transformControl.attach(object.parent instanceof THREE.Group ? object.parent : object);
                            transformControl.attach(this.getParents(object));
                        }

                        //let uuid=object.uuid;
                        // console.log(uuid);
                        this.$bus.$emit('getEvent',this.getParents(object));
                    }

                }else{
                    //当点击到场景空白处时禁用可控件
                   transformControl.detach();
                    scene.remove(transformControl);
                    let object=null;
                    this.$bus.$emit('getEvent',object);
                }
            });
        },
        //射线拾取器

     initLight() {
        directionalLight = new THREE.DirectionalLight(0xffffff); //添加了一个白色的平行光
        directionalLight.position.set(20, 50, 50); //设置光的方向
        directionalLight.name="平行光";
        scene.add(directionalLight); //添加到场景
        objs.push(directionalLight);
        ambientLight=new THREE.AmbientLight(0x222222);
        ambientLight.name="全局光";
        //添加一个全局环境光
        scene.add(ambientLight);
         objs.push(ambientLight);
    },
        // initRaycaster(){
        //     // raycaster=new THREE.Raycaster();

        // },
        //对象缓存初始化
        initCache(){
            cacheobjects=null;
        },
        initControl(){
  //可视化变换控件对象
        transformControl = new TransformControls( camera,renderer.domElement );
        transformControl.type="TransformControl";
        // scene.add( transformControl );//控件对象添加到场景对象
        transformControl.addEventListener('mouseDown',(event)=>{
            //加一个参数控制防止点击事件和拖拽事件冲突
            transing=true;
            console.log(transing);
        });
        //键盘控制可视化变换控件功能
        document.addEventListener('keyup',(event)=>{
            if(event.key=='e'){
                //E键缩放
                transformControl.mode="scale";
                return false;
            }
            if(event.key=='r'){
                //R键旋转
                transformControl.mode="rotate";
                return false;
            }
            if(event.key=='t'){
                //T键平移
                transformControl.mode="translate";
                return false;
            }

        });
        },
        LoadElement(type){

          scene.background = new THREE.CubeTextureLoader().load([
          require('@/assets/skybox/'+type+'/Left.png'),
          require('@/assets/skybox/'+type+'/Right.png'),
          require('@/assets/skybox/'+type+'/Up.png'),
          require('@/assets/skybox/'+type+'/Down.png'),
          require('@/assets/skybox/'+type+'/Front.png'),
          require('@/assets/skybox/'+type+'/Back.png')
        ]);

        },
        AddElement(type){
            if(type=='lonelyPlanet'){
                this.initBox();
            }
            else if(type=='bezierCurve'){
                this.initCone();
            }else if(type=='ethereaTone'){
                this.initCylinder();
            }else if(type=='electronicMusic'){
                this.initSphere();
            }else if(type=='blastSubwoofer'){
                this.initTorus();
            }else if(type=='BarModel'){
                this.initBars();
                this.sceneChangedEvent();
            }else if(type=='LineModel'){
                this.initLine();
                this.sceneChangedEvent();
            }
        },
        initBox(){
            var material=new THREE.MeshStandardMaterial();
            material.color=new Color(0.8,0.2,0.2);
            var box=new THREE.Mesh(new THREE.BoxGeometry(5,5,5),material);
            box.position.set(-50, 20, 0);
            box.name="盒子";
            scene.add(box);
            objs.push(box);
            this.sceneChangedEvent();
        },
       initCone(){
        var material=new THREE.MeshStandardMaterial();
        material.color=new Color(0.7,0.6,0.6);
         var cone=new THREE.Mesh(new THREE.ConeGeometry(5,20,32),material);
          cone.position.set(20, 20, 0);
          cone.name="圆锥";
           scene.add(cone);
           objs.push(cone);
          this.sceneChangedEvent();
           //renderer.render(scene,camera);//渲染


       },
       initCylinder(){
            var material=new THREE.MeshStandardMaterial();
            material.color=new Color(0.8,0.7,0.2);
           var cylinder=new THREE.Mesh(new THREE.CylinderGeometry(5, 5, 20, 32),material);
           cylinder.position.set(50,20,0);
           cylinder.name="圆柱";
           scene.add(cylinder);
           objs.push(cylinder);
           this.sceneChangedEvent();
           //renderer.render(scene,camera);//渲染

       },
       initSphere(){
           var material=new THREE.MeshStandardMaterial();
            material.color=new Color(0.2,0.7,0.2);
          var  sphere=new THREE.Mesh(new THREE.SphereGeometry(5, 32, 32),material);
           sphere.position.set(-20, -20, 0);
           sphere.name="球";
            scene.add(sphere);
            objs.push(sphere);
            this.sceneChangedEvent();



       },
       initTorus(){
           var material=new THREE.MeshStandardMaterial();
            material.color=new Color(0.3,0.1,0.9);
           var torus =new THREE.Mesh(new THREE.TorusGeometry(10, 3, 16, 100),material);
            torus.position.set(50, -20, 0);
            torus.name="圆环";
            scene.add(torus);
            objs.push(torus);
            this.sceneChangedEvent();
            //renderer.render(scene,camera);//渲染


       },
        init(){
            this.initRenderer();
            this.initScene();
            this.initCamera();
            this.initLight();
            this.initMesh();
            // this.initRaycaster();
            this.initCache();
            this.initCameraControl();
            this.initControl();
            this.initHelper();
            this.watchHelper();
            // this.initMouseEvent();
            this.initEventManager();
            this.sendid();
            this.initStats();

            // initDatGUI();
            this.sceneChangedEvent();
            this.animate();
           // console.log(scene);

        },
        watchHelper(){
            let state=this.state;
             this.$bus.$emit('stateControl',state);
        },
        sceneChangedEvent(){
            //requestAnimationFrame(this.sceneChangedEvent());
            let scenelist=scene.children;
            this.$bus.$emit('sceneChanged',scenelist);
        },
        //鼠标事务处理（包括点击事件、移动事件）
        // initMouseEvent(){
            //给canvas对象进行鼠标坐标转换
            // mouse=new Vector2();
            // let domX=0;
            // let domY=0;
            // let width=0;
            // let height=0;
            // renderer.domElement.addEventListener('mousemove',(event)=>{
            //     //console.log(event.offsetX,event.offsetY);
            //     domX=event.offsetX;
            //     domY=event.offsetY;
            //     width=renderer.domElement.offsetWidth;
            //     height=renderer.domElement.offsetHeight;
            //     mouse.x=domX/width*2-1;
            //     mouse.y=-domY*2/height+1;
            //     // console.log(mouse.x,mouse.y);
            //     raycaster.setFromCamera(mouse,camera);
            //     //防止控制器被选中
            //     scene.remove(transformControl);
            //     const intersection=raycaster.intersectObjects(scene.children,false);
            //     //把所有有children的屏蔽掉，可以防止对helper进行控制
            //     scene.add(transformControl);
            //     if(intersection.length){
            //         //如果拾取到材质物体
            //         const object=intersection[0].object;
            //         //如果当下物体不是上次缓存物体，说明是从不同物体上移到当前物体上触发mouseenter事件
            //         if(object!=cacheobjects){
            //             if(cacheobjects){
            //                 //当鼠标从一个物体移动到另一个物体时，先触发之前缓存物体的mouseleave方法
            //                 //再触发当前物体的mouseenter方法
            //                 cacheobjects.dispatchEvent({
            //             type:'mouseleave'
            //         });
            //             }
            //               object.dispatchEvent({
            //             type:'mouseenter'
            //         });
            //         }//如果相等触发mousemove方法得到物体坐标(鼠标一直在物体上移动)
            //         else if(object==cacheobjects){
            //             object.dispatchEvent({
            //             type:'mousemove'
            //         });
            //         }
            //         cacheobjects=object;
            //     }else{
            //         //当鼠标从一个物体移动到射线拾取器拾取不到物体时也会触发之前的物体的mouseleave方法
            //          if(cacheobjects){
            //                 //当鼠标从一个物体移动到另一个物体时，先触发之前缓存物体的mouseleave方法
            //                 //再触发当前物体的mouseenter方法
            //                 cacheobjects.dispatchEvent({
            //             type:'mouseleave'
            //         });
            //     }
            //         cacheobjects=null;


            //     }


            // });
            // // //物体拾取事件处理
            // renderer.domElement.addEventListener('click',(event)=>{
            //     if(transing){
            //         transing=false;
            //         return false;
            //     }
            //     // raycaster.setFromCamera(mouse,camera);
            //     // //防止控制器被选中
            //     // scene.remove(transformControl);
            //     // const intersection=raycaster.intersectObjects(scene.children,false);
            //     // //把所有有children的屏蔽掉，可以防止对helper进行控制
            //     // scene.add(transformControl);
            //     // if(intersection.length){


            //     //     const object=intersection[0].object;
            //     //     transformControl.attach(object);
            //     // }else{
            //     //     //当点击到场景空白处时禁用可控件
            //     //    transformControl.detach();
            //     // }
            // });

        // },
        initStats(){
            stats = new Stats();
             stats.domElement.style.position = 'absolute'; //绝对坐标
                stats.domElement.style.left = '0px';// (0,0)px,左上角
                stats.domElement.style.top = '0px';
            document.getElementById('webgl').appendChild(stats.domElement);

        },
        updatePosition(){
            mesh.position.set(controls.positionX,controls.positionY,controls.positionZ);
        },
        //初始化渲染器
        initRenderer(){


            renderer=new THREE.WebGLRenderer({antialias:true});//实例化渲染器
            //抗锯齿开启
            renderer.setSize(window.innerWidth*0.7,window.innerHeight*0.7);//设置宽高
            document.getElementById('webgl').appendChild(renderer.domElement);//添加到dom
        },
        //初始化场景
        initScene(){

            scene=new THREE.Scene();//实例化场景
            objs=[];
             scene.background = new THREE.CubeTextureLoader().load([
          require('@/assets/skybox/Galaxy/Left.png'),
          require('@/assets/skybox/Galaxy/Right.png'),
          require('@/assets/skybox/Galaxy/Up.png'),
          require('@/assets/skybox/Galaxy/Down.png'),
          require('@/assets/skybox/Galaxy/Front.png'),
          require('@/assets/skybox/Galaxy/Back.png')
        ]);
        //   scene.background=new THREE.Color( 0xDEE6E6 );
            // this.$bus.$emit('sceneChanged',scene);
        },
        //初始化相机
        initCamera(){
            camera=new THREE.PerspectiveCamera(45,window.innerWidth/window.innerHeight,0.1,200)
            //实例化相机】
            camera.position.set(0,0,150);
        },
        //创建模型
        initMesh(){
            // geometry = new THREE.BoxGeometry(5, 5, 5 ); //创建几何体
            // var material=new THREE.MeshStandardMaterial();
            // material.color=new Color(0.7,0.2,0.2); //创建材质

            // mesh = new THREE.Mesh( geometry, material ); //创建网格
            // mesh.name="盒子";

            // scene.add( mesh ); //将网格添加到场景


            // this.initCone();
            // this.initBox();
            this.initLine();
            this.initBars();

        },

        initBars(){
            this.audioBars(25,128);
        },

        audioBars(radius, countData) {
      let barGroup = new THREE.Group();
      let R = radius;
      let N = countData;
      for (let i = 0; i < N; i++) {
        let minGroup = new THREE.Group();
        let box = new THREE.BoxGeometry(1, 1, 1);
        let material = new THREE.MeshPhongMaterial({
          color: 0x00ffff
        }); // 材质对象
        let m = i;
        let mesh = new THREE.Mesh(box, material);

        mesh.position.y = 0.5;
        minGroup.add(mesh);
        minGroup.position.set(
          Math.sin(((m * Math.PI) / N) * 2) * R,
          Math.cos(((m * Math.PI) / N) * 2) * R,
          0
        );
       minGroup.rotation.z = ((-m * Math.PI) / N) * 2;
        barGroup.add(minGroup);
      }
      barGroup.name="音频谱";
      barGroup.type="BarGroup";
      scene.add(barGroup);
      objs.push(barGroup);
    },
/* 这里有个原理，为什么对音频线和音频组进行移动旋转操作后 依然可以根据初始化的音频数据进行重画
按原理来说应该要重新计算各个线段的坐标值进行重绘，为什么这里导出数据后依然可以利用初始化的
barNodes进行一个线段计算重回吧，原因是因为，在编辑器中对一个组对象进行操作时，其各子对象
的坐标并不会变化，只改变这个组对象的位置、旋转等信息，其子对象的坐标一直是相对于其父对象的
坐标即本地坐标，而非世界坐标，只有当父对象是scene场景时，本地坐标才等于世界坐标
 */
        initLine(){
            this.audioLines(20,256);
        },
        renderGeometries(vertices) {
            const res = [];
            vertices = vertices.concat(vertices[0]);
            //合并数组，把里外位置告知要画的线
            vertices.forEach(value => {
            res.push(value.x, value.y, 0);
      });
            return new THREE.BufferAttribute(new Float32Array(res), 3);
    },
        audioLines(radius, countData) {
            var barNodes = range(0, countData).map(index => {
                return new node(
                radius,
                ((index / countData) * 360 + 45) % 360,
                new THREE.Vector2(0, 0)
                );
            });
            console.log(barNodes);
            //得到一个记录圆上256个点的位置信息的数组
            const lineMaterial = new THREE.LineBasicMaterial({
                color: 0x00ffff
            });
            var barLine = range(0, countData).map(index => {
            return new THREE.Line(
                new THREE.BufferGeometry().setAttribute(
                    "position",
                this.renderGeometries([
                    barNodes[index].positionA(),
                     barNodes[index].positionB()
            ])
          ),
            lineMaterial
            );
        });
        var outLine = new THREE.Line(
        new THREE.BufferGeometry().setAttribute(
          "position",
          this.renderGeometries(barNodes.map(node => node.positionA()))
        ),
        lineMaterial
      );

      var inLine = new THREE.Line(
        new THREE.BufferGeometry().setAttribute(
          "position",
          this.renderGeometries(barNodes.map(node => node.positionB()))
        ),
        lineMaterial
      );

      var linesGroup = new THREE.Group();
      linesGroup.add(outLine);
      linesGroup.add(inLine);
      barLine.forEach(line => linesGroup.add(line));
      linesGroup.name="音频线";
      linesGroup.type="LineGroup";
      scene.add(linesGroup);
      objs.push(linesGroup);
    },



        update(){
            stats.update();
        },
        Reset(){

        },
        ResetAll(){
            this.$bus.$on('reset',()=>{
                this.Reset();
            });
        },
        Export(){
            let that=this;
            this.$bus.$on('export',()=>{
            // if(scene.children.length>0){
            //     let obj;
            //     //删除所有helper，只保留光源、材质
            //     for(var i=scene.children.length-1;i>=0;i--){
            //
            //          obj=scene.children[i];
            //         if(obj.type.indexOf('Helper')!=-1||obj.type.indexOf('Control')!=-1){
            //              scene.remove(obj);
            //         }
            //     }
            // }
              that.RemoveHelperAndControl();

                //因为每次迭代中.children 一旦remove后从头开始执行
                //数组会改变，因此使用遍历的forEach也好，traverse也好数组的
                //索引全都改变，只会删除一个对象
                //但如果从后开始遍历删除，每次remove后前面的索引也即是未遍历过的节点索引不会变
                //使用state值然后进行深度监听依然不行，因为watch方法每次在mounted之后
                //这就导致了如果mounted中的data值变化，我需要导出的数据还是原来数据，watch只会在之后
                //发生作用。

            var JSONStr=JSON.stringify(scene.toJSON());

            this.download("scene.json",JSONStr);
            that.AddHelperAndControl();
            });
        },
        download(filename,text){
            var pom=document.createElement('a');
            pom.setAttribute('href','data:text/plain;charset=utf-8,'
            +encodeURIComponent(text));
            pom.setAttribute('download',filename);
            if(document.createEvent){
                var event=document.createEvent('MouseEvents');
                event.initMouseEvent('click',true, false,
                window,0,0,0,0,0,false,false,false,false,0,null);
                pom.dispatchEvent(event);
            }else{
                pom.click();
            }

        },
        //运行动画
        animate(){
            id=requestAnimationFrame(this.animate);//循环调用函数

            // mesh.rotation.x+=0.01;
            // // //每帧网格模型沿x轴旋转0.01弧度
            // mesh.rotation.y+=0.02;
            // //每帧网格模型沿y轴旋转0.02弧度
            // //stats.update();//更新性能检测框

            this.update();
            renderer.render(scene,camera);//渲染
        },
      sendres(){
          this.$bus.$on('sendres',(res)=>{
           var loader=new THREE.ObjectLoader();
           loader.parse(res,function (obj){
             scene=obj;

           })
          })
      },
      requestsave(){
          let that=this;
        this.$bus.$on('requestsave',()=>{
          // if(scene.children.length>0){
          //   let obj;
          //   //删除所有helper，只保留光源、材质
          //   for(var i=scene.children.length-1;i>=0;i--){
          //
          //     obj=scene.children[i];
          //     if(obj.type.indexOf('Helper')!=-1||obj.type.indexOf('Control')!=-1){
          //       scene.remove(obj);
          //     }
          //   }
          // }
          that.RemoveHelperAndControl();
          var JSONStr=JSON.stringify(scene.toJSON());
          let file=new File([JSONStr],'scene.json',{
            type: 'application/json',
            lastModified: Date.now()
          });
          //组合成文件
          let param=new FormData();
          param.append('file',file);
          let config={headers:{'Content-Type':'multipart/form-data'}}
          this.request.post("http://localhost:9090/upload",
            param,config).then(res=>{
            // console.log(res);
            that.$bus.$emit('responsesave',res.fileid);
            console.log(res.fileid);
            console.log(scene);
            that.AddHelperAndControl();
          });
          //提交表单文件返回id


        });
      },
      sendid(){
        let id=null;
        this.$bus.$emit('sendfileid',id);
      },
      startedit(){

          let that=this;
        this.$bus.$on('eidtfile',(res,id)=>{
          //objs=[];
          //因为每次跳转路由都会重新初始化其他工具，这里先删除
          //把json数据打包成文件通过url进行传输
          var JSONStr=JSON.stringify(res);
          let file=new File([JSONStr],'scene.json',{
            type: 'application/json',
            lastModified: Date.now()
          });
          //组合成文件
          let url = null ;
          if (window.createObjectURL!=undefined) { // basic
            url = window.createObjectURL(file) ;
          } else if (window.URL!=undefined) { // mozilla(firefox)
            url = window.URL.createObjectURL(file) ;
          } else if (window.webkitURL!=undefined) { // webkit or chrome
            url = window.webkitURL.createObjectURL(file) ;
          }


          //that.RemoveHelperAndControl();
          var loader=new THREE.ObjectLoader();
          loader.load(url,
            function(obj){
             //let newobjs=[];
            that.RemoveHelperAndControl();
            scene=obj;
            that.AddHelperAndControl();
              // if(obj.children.length>0){
              //   let childobj;
              //   for(var i=obj.children.length-1;i>=0;i--){
              //     childobj=obj.children[i];
              //     if(childobj.type.indexOf('Object3D')!=-1||childobj.type.indexOf('Mesh')!=-1){
              //       newobjs.push(childobj);
              //     }
              //     if((childobj.type.indexOf('Light')!=-1&&childobj.type.indexOf('Helper')==-1)){
              //       newobjs.push(childobj);
              //     }
              //   }
              // }
              // objs=newobjs;
              // console.log(objs);


              // if(scene.children.length>0){
              //   let childobj;
              //   //删除所有可操作对象
              //   for(var i=scene.children.length-1;i>=0;i--){
              //     childobj=scene.children[i];
              //     if((childobj.type.indexOf('Light')!=-1&&childobj.type.indexOf('Helper')==-1)||childobj.type.indexOf('Group')!=-1||childobj.type.indexOf('Mesh')!=-1){
              //       scene.remove(childobj);
              //
              //     }
              //
              //   } console.log(scene);
                //console.log(objs);
              // }
              //加载完成后进行
              // scene.background=obj.background;
              // if(obj.children.length>0) {
              //   let childobj;
              //   for (var i = obj.children.length - 1; i >= 0; i--) {
              //     childobj = obj.children[i];
              //     obj.remove(childobj);
              //     childobj.parent=scene;
              //     scene.add(childobj);
              //     objs.push(childobj);
              //   }
              // }
              eventManager.RemoveEvent();
             that.initEventManager();
              that.sceneChangedEvent();
              //console.log(objs);
            },
            // onProgress callback
            function ( xhr ) {
              console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );
            },

            // onError callback
            function ( err ) {
              console.error( 'An error happened'+err );
            });
          // loader.parse(res,function (obj){
          //
          //  }
          //  console.log(scene);
            //console.log(scene);
           //  that.AddHelperAndControl();
           //  //需要初始化一下objs
           //  if(scene.children.length>0){
           //    let obj;
           //    //删除所有helper，只保留光源、材质
           //    objs=[];
           //    //初始化一下
           //    for(var i=scene.children.length-1;i>=0;i--){
           //
           //      obj=scene.children[i];
           //      if(obj.type.indexOf('Mesh')!=-1){
           //        //说明是需要操作的物体
           //        objs.push(obj);
           //      }
           //      if(obj.type.indexOf('Object3D')!=-1){
           //        objs.push(obj);
           //      }
           //      // if(obj.type.indexOf('Light')!=-1&&obj.type.indexOf('Helper')==-1){
           //      //   objs.push(obj);
           //      // }
           //    }
           //    console.log(objs);
           //  }
           //  transing=false;
           //
           // // that.initEventManager();
          //    that.sceneChangedEvent();
          //
          // });
          that.$bus.$emit('sendfileid',id);

        });
      },
      addnew(){
          let that=this;
          this.$bus.$off('addfile');
        this.$bus.$on('addfile',()=>{
          let id=null;
          that.$bus.$emit('sendfileid',id);
          //设置个时间防止 加载问题

          that.RemoveHelperAndControl();
          that.initScene();
          that.initLight();
          that.initMesh();
          that.AddHelperAndControl();
          eventManager.RemoveEvent();
          that.initEventManager();
          that.sceneChangedEvent();
        });
      }
    },

  mounted() {
        this.init();
        this.CheckFromTree();
        this.findcurrentObj();
        this.AddModel();
        this.ResetAll();
        this.Export();
        this.sendres();
        this.requestsave();
        this.startedit();
        this.addnew();
    },
}
</script>
<style>
 #webgl {
    height: 500px;
  }
.app-container{
    background-color: #432d43;
}
</style>
