<template>
   <div class="app-container-play">

     <el-container>
       <el-header>
         <el-button @click="Import" type="primary" style="margin-left: 16px;">
           导入
         </el-button>
       </el-header>
       <el-main>
         <el-scrollbar class="Scrollbar">
           <div id="webgl" style="position:relative">

           </div>
         </el-scrollbar>
       </el-main>
       <el-footer height="100px">
         <AudioPlayer/>
       </el-footer>
     </el-container>


     <!-- <div class="file-select-btn" @click="$refs.fileId.click()">
      选择音乐
      <input ref="fileId" type="file" class="file-input" @change="getAudio" />
     </div> -->


  </div>
</template>

<script>
import * as THREE from 'three/build/three.module.js'
import Stats from '../../store/Stats/stats.min.js'
import { OrbitControls } from "../../lib/OrbitControls.js";
import index, { GUI } from "../../lib/dat.gui.module.js";
//辉光
import { EffectComposer } from "three/examples/jsm/postprocessing/EffectComposer.js";
import { RenderPass } from "three/examples/jsm/postprocessing/RenderPass.js";
import { GlitchPass } from 'three/examples/jsm/postprocessing/GlitchPass.js';
import { UnrealBloomPass } from "three/examples/jsm/postprocessing/UnrealBloomPass.js";

import { ShaderPass } from "three/examples/jsm/postprocessing/ShaderPass.js";
import { CopyShader } from "three/examples/jsm/shaders/CopyShader.js";
import { range } from "../../components/mode/range";
import { node } from "../../components/mode/node";
import AudioPlayer from "../scene/Player/Player.vue"
import { randomRange } from "../../components/mode/randomRange";
import { Triangle } from "../../components/mode/Triangle";
import { Loader } from '../../lib/three.module.js';
let renderer,camera,scene,geometry,material,mesh,stats,controls;
//  let container=document.getElementById('webgl');
let gui = {
  R: 20,
  G: 90,
  B: 225,
  TrianglesBgColor: 0x03a9f4,
  TrianglesLineColor: 0x03a9f4,
  lineColor: 0x00ffff,
  rotate: false
};
let audio, analyser; // 音频
let BarNodes; // 线
let composer;//合成器
let Triangles = [],
  TriangleGroup;
let LineGroups=[];
let BarGroups=[];
let GeometryGroups=[];
let map=[];
let ColorMap=[];
export default {
    name: 'Scene',
    components:{
      AudioPlayer
    },
    data() {
      return {
        clock:new THREE.Clock(),
        scale:1,
        Analyser:null,
        isPlaying:false,
        isLoaded:false,
      }
    },
    watch:{
      isLoaded:{
        handler(){
          if(this.isLoaded==true){
             this.initLight();
              this.AnalysisLine();
              this.initBars();
              this.initEffectComposer();
              //that.initStats();
            // initDatGUI();
            this.animate();
          }
        }
      }
      // $route:{
      //   handler(){
      //     this.
      //   }
      // }
    },
    methods: {
      Import(){
        this.isLoaded=false;
        //每次导入改变值然后重新渲染
        let that=this;
        let input=document.createElement('input');
        input.type="file";
        input.addEventListener("change",function(){
          let file=input.files[0];
          console.log(file);
          if(file.type.indexOf("json")>=0){
            let url = null ;
            if (window.createObjectURL!=undefined) { // basic
                url = window.createObjectURL(file) ;
            } else if (window.URL!=undefined) { // mozilla(firefox)
                url = window.URL.createObjectURL(file) ;
            } else if (window.webkitURL!=undefined) { // webkit or chrome
                url = window.webkitURL.createObjectURL(file) ;
            }
            //根据url进行加载回调，可以防止
        //     let reader=new FileReader();
        //     //reader.readAsText(file);
        //     reader.readAsDataURL(file);
        // reader.onloadend = function(e) {
        //   _that.audioLoad(e.target.result);
        // };


             let loader=new THREE.ObjectLoader();
             loader.load(url,
              function(obj){
                //加载完成后进行
                scene=obj;
                that.isLoaded=true;

              },
              // onProgress callback
	            function ( xhr ) {
		          console.log( (xhr.loaded / xhr.total * 100) + '% loaded' );
	            },

	          // onError callback
	            function ( err ) {
		              console.error( 'An error happened'+err );
              });

            // let reader=new FileReader();
            // reader.readAsText(file);
            // reader.onloadend=function(){
            //   let loader=new THREE.ObjectLoader();
            //   loader.parse(JSON.parse(this.result),
            //     function(obj){
            //       scene=obj;
            //     }
            //   );
              //scene=newscene;
          }
        });
        input.click();
      },
      // 选择音频
    // getAudio() {
    //   let _that = this;
    //   let objFile = this.$refs.fileId;
    //   if (objFile.value === "") {
    //     return false;
    //   }
    //   if (window.FileReader) {
    //     let reader = new FileReader();
    //     reader.readAsDataURL(objFile.files[0]);
    //     reader.onloadend = function(e) {
    //       _that.audioLoad(e.target.result);
    //     };
    //   }
    // },
    //  音频加载播放
    // audioLoad(url) {
    //   // let _that = this;

    //   let audioLoader = new THREE.AudioLoader(); // 音频加载器
    //   audioLoader.load(url, function(AudioBuffer) {
    //     if (audio.isPlaying) {
    //       audio.stop();
    //       audio.setBuffer();
    //     }
    //     audio.setBuffer(AudioBuffer); // 音频缓冲区对象关联到音频对象audio
    //     audio.setLoop(true); //是否循环
    //     audio.setVolume(1); //音量
    //     audio.play(); //播放
    //     // 音频分析器和音频绑定，可以实时采集音频时域数据进行快速傅里叶变换
    //     analyser = new THREE.AudioAnalyser(audio, 256 * 2);
    //   });
    // },
    GetState(){
      this.$bus.$on('PlayState',(isPlaying)=>{
          this.isPlaying=isPlaying;
      })
    },
    GetAudioData(){
      let that=this;
        this.$bus.$on('BindingAnalyser',(analyser)=>{
          that.Analyser=analyser;

        })
    },
    initAudio(){
      let listener=new THREE.AudioListener();
      audio=new THREE.Audio(listener);
    },
      AnalysisLine(){


        //获取变化线段
        scene.traverse(function(obj){
          if(obj.name.indexOf("音频线")!=-1){
            LineGroups.push(obj);
            map.push({
              uuid:obj.uuid,
              x:obj.scale.x,
              y:obj.scale.y,
              z:obj.scale.z
            });

          }else if(obj.name.indexOf("音频谱")!=-1){
            BarGroups.push(obj);
            map.push({
              uuid:obj.uuid,
              x:obj.scale.x,
              y:obj.scale.y,
              z:obj.scale.z
            });
           // console.log(obj.children[0].children[0]);
            ColorMap.push({
              uuid:obj.uuid,
              r:obj.children[0].children[0].material.color.r,
              g:obj.children[0].children[0].material.color.g,
              b:obj.children[0].children[0].material.color.b,
            });
            console.log(obj.children[0].children[0].material.color);
            console.log(ColorMap);

          }else if(obj.type.indexOf('Mesh')!=-1&&obj.parent==scene){
            //几何体
            GeometryGroups.push(obj);
            map.push({
              uuid:obj.uuid,
              x:obj.scale.x,
              y:obj.scale.y,
              z:obj.scale.z

            });
            console.log(GeometryGroups);

          }
        });
      },
       updateCircle(linesGroup,x,y,z) {
      if (BarNodes) {

        linesGroup.scale.set(this.scale*x,this.scale*y,this.scale*z);

        const geometryA = linesGroup.children[0].geometry;
        //outline
        const AttributeA = geometryA.getAttribute("position");
        const geometryB = linesGroup.children[1].geometry;
        //inline
        const AttributeB = geometryB.getAttribute("position");

        const positions = BarNodes.map(value => {
          return [value.positionA(), value.positionB()];
        });
        positions.forEach((position, index) => {
          AttributeA.set([position[0].x, position[0].y], index * 3);
          AttributeB.set([position[1].x, position[1].y], index * 3);
          //前两线是outline和inline
          const geometry = linesGroup.children[index+2].geometry;
          const Attribute = geometry.getAttribute("position");
          Attribute.set(
            [position[0].x, position[0].y, 0, position[1].x, position[1].y, 0],
            0
          );
          Attribute.needsUpdate = true;
        });
        AttributeA.set(
          [AttributeA.array[0], AttributeA.array[1]],
          positions.length * 3
        );
        AttributeB.set(
          [AttributeB.array[0], AttributeB.array[1]],
          positions.length * 3
        );
        AttributeA.needsUpdate = true;
        AttributeB.needsUpdate = true;
      }
    },

        init(){
            this.initRenderer();
            this.initScene();
            this.initCamera();
            this.initControls();

            this.initAudio();
            //this.initEffectComposer();
            this.initStats();
            // initDatGUI();
            //this.animate();

        },
        initControls() {
          controls = new OrbitControls(camera, renderer.domElement);
         // 如果使用animate方法时，将此函数删除
         //controls.addEventListener( 'change', render );
          // 使动画循环使用时阻尼或自转 意思是否有惯性
          controls.enableDamping = true;
          //动态阻尼系数 就是鼠标拖拽旋转灵敏度
         //controls.dampingFactor = 0.25;
          //是否可以缩放
          controls.enableZoom = false;
          //是否自动旋转
          controls.autoRotate = gui.rotate;
          //设置相机距离原点的最远距离
          controls.minDistance = 1;
          //设置相机距离原点的最远距离
          controls.maxDistance = 200;
          //是否开启右键拖拽
          controls.enablePan = false;
        },
        initLight() {
          scene.add(new THREE.AmbientLight(0x444444));
          let light = new THREE.PointLight(0xffffff);
          light.position.set(80, 100, 50);
          //告诉平行光需要开启阴影投射
          light.castShadow = true;
          scene.add(light);
        },
        initStats(){
             stats = new Stats();
             stats.domElement.style.position = 'absolute'; //绝对坐标
             stats.domElement.style.left = '0px';// (0,0)px,左上角
             stats.domElement.style.top = '0px';
            document.getElementById('webgl').appendChild(stats.domElement);

        },
        // updatePosition(){
        //     mesh.position.set(controls.positionX,controls.positionY,controls.positionZ);
        // },
        //初始化渲染器
        initRenderer(){


            renderer=new THREE.WebGLRenderer({ antialias: true });//实例化渲染器
            renderer.setSize(window.innerWidth*0.85,window.innerHeight*0.85);//设置宽高
            document.getElementById('webgl').appendChild(renderer.domElement);//添加到dom
        },
        //初始化场景
        initScene(){

            scene=new THREE.Scene();//实例化场景
        //     scene.background = new THREE.CubeTextureLoader().load([
        //   require('@/assets/skybox/right.jpg'),
        //   require('@/assets/skybox/left.jpg'),
        //   require('@/assets/skybox/top.jpg'),
        //   require('@/assets/skybox/bottom.jpg'),
        //   require('@/assets/skybox/front.jpg'),
        //   require('@/assets/skybox/back.jpg')
        // ]);

        },
        //初始化相机
        initCamera(){
            camera=new THREE.PerspectiveCamera(45,window.innerWidth/window.innerHeight,0.1,200)
            //实例化相机】
            camera.position.set(0,0,150);
        },
        //创建模型


        //运行动画
        animate(){
            requestAnimationFrame(this.animate);//循环调用函数

            // mesh.rotation.x+=0.01;
            // // //每帧网格模型沿x轴旋转0.01弧度
            // mesh.rotation.y+=0.02;
            // //每帧网格模型沿y轴旋转0.02弧度
            // //stats.update();//更新性能检测框

            stats.update();
            controls.update();
            if(this.Analyser&&this.isPlaying){
              let bufferLength=this.Analyser.frequencyBinCount;
              let arr=new Uint8Array(bufferLength);
              this.Analyser.getByteFrequencyData(arr);
              //let arr=analyser.getFrequencyData();
              //console.log(arr);
              const Delta=this.clock.getDelta();
              BarNodes.forEach((node,index,array)=>{
                node.strength(arr[index % array.length]*0.1);
                node.transition(Delta);

              });
              let that=this;
               this.scale=1+arr[Math.ceil(arr.length*0.05)]/500;
              //let nscale=1+arr[13]/500;
              //console.log("原始"+this.scale+"改进"+nscale);

               if(LineGroups.length>0){
                  LineGroups.forEach(function(obj){
                    const scaledata=map.filter((item)=>item.uuid==obj.uuid);

                    that.updateCircle(obj,scaledata[0].x,scaledata[0].y,scaledata[0].z);
                });
               }
               if(BarGroups.length>0){
                 BarGroups.forEach(function(obj){

                    const scaledata=map.filter((item)=>item.uuid==obj.uuid);
                    const BarColor=ColorMap.filter((item)=>item.uuid==obj.uuid);
                    // console.log(BarColor[0].r);
                    // console.log(BarColor[0].g);
                    // console.log(BarColor[0].b);
                    that.updateBars(arr,obj,scaledata[0].x,scaledata[0].y,scaledata[0].z,
                                   BarColor[0].r,BarColor[0].g,BarColor[0].b);
                 });
               };
               if(GeometryGroups.length>0){
                 GeometryGroups.forEach(function (obj){
                   const scaledata=map.filter((item)=>item.uuid==obj.uuid);
                   that.updateGeometry(obj,scaledata[0].x,scaledata[0].y,scaledata[0].z);

                 });
               }


            }
            composer.render();//使用后期处理通道渲染
        },
        updateGeometry(Geometry,x,y,z){
            Geometry.scale.set(this.scale*x,this.scale*y,this.scale*z);
        },
        updateBars(arr,barGroup,x,y,z,r,g,b){
           if(barGroup){
              barGroup.rotation.z += 0.002;
              barGroup.scale.set(this.scale*x, this.scale*y, this.scale*z);
              barGroup.children.forEach((elem,index)=>{
                //记录的原rgb值是0-1的浮点值，需要根据频率数值转换
                if(r>0){
                   elem.children[0].material.color.r= arr[index] *r/ 255;

                }
               if(g>0){
                  elem.children[0].material.color.g=arr[index]*g/255;
               }

                if(b>0){
                  elem.children[0].material.color.b=arr[index]*b/255;
                }

                if(arr[index]===0){
                  elem.scale.set(1,1,1);
                }else{
              let m = arr[index] / 20;
                   let targetRange = Math.max(
                  arr[index] / 20 - arr[index - 1] / 20,0);
              if (m < targetRange) {
                m = targetRange;
              }

              elem.scale.set(1, m, 1);
                }

              });
           }
        },
        initBars(){
          BarNodes=range(0, 256).map(index => {
            return new node(
              20,
              ((index / 256) * 360 + 45) % 360,
              new THREE.Vector2(0, 0)
            );
          });
          console.log(BarNodes);
        },
        //后期处理
        initEffectComposer(){
            composer=new EffectComposer(renderer);
            var renderPass=new RenderPass(scene,camera);
            composer.addPass(renderPass);
            //RenderPass通常位于过程链的开始，以便将渲染好的场景作为输入来提供给下一个后期处理步骤。
            //CopyShader是为了能将结果输出，普通的通道一般都是不能输出的，要靠CopyShader进行输出
             const copyShader = new ShaderPass(CopyShader);
            copyShader.renderToScreen = true;
            //这个参数的目的是马上将当前的内容输出
            this.initBloomPass();
            composer.addPass(copyShader);
        },
        initBloomPass(){
          let params = {
            exposure: 0.5,
            bloomStrength: 1,
             bloomThreshold: 0,
            bloomRadius: 0.8
          };
          let bloomPass=new UnrealBloomPass(
            new THREE.Vector2(window.innerWidth*0.85,window.innerHeight*0.85),
              1.5,
              0.2,
              0
          );
          bloomPass.threshold = params.bloomThreshold;
          bloomPass.strength = params.bloomStrength;
          bloomPass.radius = params.bloomRadius;
          composer.addPass(bloomPass);

        },
      startplay(){
        let that=this;
        this.$bus.$on('playfile',(res)=>{
          //console.log(res);
          //that.isLoaded=false;
          //每次导入改变值然后重新渲染
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
          var loader=new THREE.ObjectLoader();
          loader.load(url,function (obj){
            scene=obj;
            //scene.background=new THREE.Color( 0xDEE6E6 );
            console.log(scene);
            //that.initLight();
            that.AnalysisLine();
            that.initBars();
            that.initEffectComposer();
            //that.initStats();
            // initDatGUI();
            that.animate();
          });

        });
      }

    },
    mounted() {
        this.init();
        this.GetAudioData();
        this.GetState();
        this.startplay();

    },
}
</script>

<style>

</style>
