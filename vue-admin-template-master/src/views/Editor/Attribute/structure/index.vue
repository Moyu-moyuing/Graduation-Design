<template>
    <div class="structure">
        <el-scrollbar class="Scrollbar">
        <el-tree ref="ElTree" :data="sceneStructure" default-expand-all :highlight-current="true" node-key="uuid" :props="defaultProps" @node-click="handleNodeClick"
        :current-node-key="current">
            <template #default="{ data }">
                <span class="el-tree-node__label" :id="data.uuid">{{ data.name }}</span>
            </template>
        </el-tree>
        <div class="object" >
            <div class="Panel" >
                <div class="Row">
                    <span class="Text">类型</span>
                    <span class="Text">{{Object3D.type}}</span>
                </div>
                <div class="Row">
                    <span class="Text">识别码</span>
                    <input class="UUIDInput" autocomplete="off" disabled="" v-model="Object3D.uuid">
                    <!-- <button class="Button" style="margin-left: 7px;">更新</button> -->
                </div>
                <div class="Row">
                    <span class="Text">名称</span>
                    <input class="NameInput" autocomplete="off" v-model="Object3D.name" @change="RefreshNodeName">
                </div>
                <div class="Row">
                    <span class="Text" >位置</span>
                    <input class="Number" autocomplete="off" v-model="Object3D.position.x">
                    <input class="Number" autocomplete="off" v-model="Object3D.position.y">
                    <input class="Number" autocomplete="off" v-model="Object3D.position.z">
                </div>
                <div class="Row">
                    <span class="Text" >旋转</span>
                    <input class="Number" autocomplete="off" v-model="RotationX">
                    <input class="Number" autocomplete="off" v-model="RotationY">
                    <input class="Number" autocomplete="off" v-model="RotationZ">
                </div>
                <div class="Row">
                    <span class="Text" >缩放</span>
                    <input class="Number" autocomplete="off" v-model="Object3D.scale.x">
                    <input class="Number" autocomplete="off" v-model="Object3D.scale.y">
                    <input class="Number" autocomplete="off" v-model="Object3D.scale.z">
                </div>
                <div class="Row">
                    <span class="Text" >可见性</span>
                    <el-checkbox v-model="Object3D.visible"></el-checkbox>
                </div>
                 <div class="Row">
                    <span class="Text" >颜色</span>
                    <div class="block">
                       <el-color-picker v-model="color" ></el-color-picker>
                    </div>
                </div>


            </div>
        </div>
        </el-scrollbar>
    </div>

</template>

<script>
 /* 需要添加几个方法和需求：
 1、改变颜色 √
 2、改变欧拉角变化 √
 3、改变名称，场景树也改变 √
 4、根据模型不同显示的编辑也不同 0
 5、设置方法 √
 6、撤销与反撤销
 7、保存与导入
 8、音频可视化已写好的模型
 9、根据文件选择可视化
 10、防止刷新
 11、帮助 √
 12、组对象控制
 13、helper的tree刷新问题
 14、首页
 15、文件区（是否需要数据库？）
 16、历史记录？
  */
import * as THREE from 'three/build/three.module.js'
import GetUUID from '../../../../lib/Getuuid';
import { Group, Object3D, Vector3 } from '../../../../lib/three.module';
export default {

    name:'structure',
    mounted() {
        this.RefreshSceneTree();//刷新生效
        this.GetObject();
        this.GetSceneObject();

    },
    computed:{
        RotationX:{
            get(){
                return THREE.Math.radToDeg(this.Object3D.rotation.x);
            },
            set(X){
                this.Object3D.rotateX(THREE.Math.degToRad(X));
            }
        },
        RotationY:{
            get(){
                return THREE.Math.radToDeg(this.Object3D.rotation.y);
            },
             set(Y){
                this.Object3D.rotateY(THREE.Math.degToRad(Y));
            }
        },
        RotationZ:{
            get(){
                return THREE.Math.radToDeg(this.Object3D.rotation.z);
            },
             set(Z){
                this.Object3D.rotateZ(THREE.Math.degToRad(Z));
            }
        },
        color:{
            get(){
                if(this.Object3D.hasOwnProperty('isTrans')){
                    return this.Object3D.color;
                }
                else{
                    if(this.Object3D.name.indexOf('音频线')!=-1){
                        return '#'+this.Object3D.children[0].material.color.getHexString();
                    }else if(this.Object3D.name.indexOf('音频谱')!=-1){
                         return '#'+this.Object3D.children[0].children[0].material.color.getHexString();
                    }
                    else{
                         if(this.Object3D.material){
                //如果材质存在
                return '#'+this.Object3D.material.color.getHexString();

                }else{
                //有些对象直接可以获取color或者是从控制面板传入的对象
                     return '#'+this.Object3D.color.getHexString();
                }
                    }

                }

            },
            set(newcolor){
                if(this.Object3D.name.indexOf('音频线')!=-1||this.Object3D.name.indexOf('音频谱')!=-1){
                     this.Object3D.traverse(function(child) {
                    if (child.isLine)
                        child.material = new THREE.LineBasicMaterial({
                         color: newcolor
                        });
                        // if(child.isMesh){
                        //     child.material = new THREE.MeshPhongMaterial({
                        //     color: newcolor
                        //     });
                        // }
                        //说明是barGroup
                        if(child.type=='Group'){
                            child.children[0].material = new THREE.MeshPhongMaterial({
                            color: newcolor
                            });
                        }
                    });
                }
                else{
                    if(this.Object3D.material){
                 this.Object3D.material.color.set(newcolor);
            }else if(this.Object3D.type.indexOf("Light")!=-1){
                //说明是光
                this.Object3D.color.set(newcolor);
            }
                }

            }
        }
    },
    watch:{
        //监听当前current变化
        current(uuid){
            if(uuid){
                this.$refs.ElTree.setCurrentKey(uuid);
            }
            else{
                this.$refs.ElTree.setCurrentKey(null);
            }

        }
    },
    methods: {
        // getScene(){
        //     console.log(this.$parent.$parent.$parent.getScene());
        // }
        // ObjectTranslate(SceneObject){

        //      let Object3D={
        //         name:SceneObject.name,
        //         uuid:SceneObject.uuid,
        //         type:SceneObject.type,
        //         position:SceneObject.position,
        //         //欧拉角
        //         rotation:new THREE.Vector3(THREE.Math.radToDeg(SceneObject.rotation.x),THREE.Math.radToDeg(SceneObject.rotation.y),THREE.Math.radToDeg(SceneObject.rotation.z)),
        //         scale:SceneObject.scale,
        //         visible:SceneObject.visible,
        //         color:'#000000',
        //         isTrans:true//判断选取对象方法传入的对象是否是原生three对象

        //     };
        //     if(SceneObject.material){
        //         //如果材质存在
        //         Object3D.color='#'+SceneObject.material.color.getHexString();

        //     }else{
        //         //有些对象直接可以获取color或者是从控制面板传入的对象
        //         Object3D.color='#'+SceneObject.color.getHexString();
        //     }
        //     return Object3D;


        // },
        // selectObjChange(color){
        //     if(this.Object3D.material){
        //          this.Object3D.material.color.set(color);
        //     }else if(this.Object3D.light){
        //         this.Object3D.color.set(color);
        //     }

        // },
        // getChildren(obj){
        //     if(!obj.children.children){
        //         return obj;
        //     }else{
        //         while(obj.children){
        //             obj=obj.children;
        //         }
        //         return obj;
        //     }
        // },
        initObject(){
             let Object3D={
                name:'',
                uuid:'',
                type:'',
                position:new THREE.Vector3(),
                //欧拉角
                rotation:new THREE.Euler(),
                scale:new THREE.Vector3(),
                visible:true,
                color:'#111111',
                isTrans:true
            };
            return Object3D;
        },
        ObjectTo3D(){

        },
        GetObject(){
            this.$bus.$on('getEvent',(object)=>{
                //选中物体触发点击事件
               this.handleNodeClick(object);


            });
        },
        GetSceneObject(){
            this.$bus.$on('responseObj',(object)=>{
                if(object.type.indexOf("Mesh")!=-1||object.type.indexOf("Group")!=-1){
                    //说明是材质物体
                    this.Object3D=object;
                }
                else if(object.type.indexOf("Light")!=-1){
                    //说明是光
                    this.Object3D=object;
                    console.log(this.Object3D);
                }
                else if(object.type.indexOf("Helper")!=-1){
                    //说明是辅助工具
                    this.Object3D=this.initObject();
                }



            });
        },
        refreshNodeByuuid(list){
            //遍历场景树结构更改名称
            list.map(c=>{
                if(c.uuid&&c.uuid==this.Object3D.uuid){
                    c.name=this.Object3D.name;
                    return;
                }
               else{

                if(c.children&&c.children.length>0){
                    this.refreshNodeByuuid(c.children);
                }
             }
            })
        },
        RefreshNodeName(){
            this.refreshNodeByuuid(this.sceneStructure);
        },
        RefreshSceneTree(){
            //每次场景结构改变刷新场景树
                this.$bus.$on('sceneChanged',(scenelist)=>{
               let sceneTree=[];
               let lightTree=[];
               let helperTree=[];
               this.createSceneStructure(scenelist,sceneTree,lightTree,helperTree);
                this.sceneStructure[0].children=sceneTree;
                this.sceneStructure[2].children=lightTree;
                this.sceneStructure[3].children=helperTree;

            });
        },
        createSceneStructure(scenelist,sceneTree,lightTree,helperTree){
            scenelist.map(c=>{

                let data={
                    name:c.name,
                    type:c.type,
                    uuid:c.uuid
                };
                if(c.children&&c.children.length>0){
                    data.children=[];
                    this.createSceneStructure(c.children,data.children,data.children,data.children);
                }
                if(c.type.indexOf("Mesh")!=-1||c.type.indexOf("Group")!=-1||c.type.indexOf("Object3D")!=-1){
                    //说明是材质物体
                    if(c.parent.name!="音频线"&&c.parent.name!="音频谱"){
                        sceneTree.push(data);
                    }

                }
                else if(c.type.indexOf("Light")!=-1){
                    //说明是光
                    lightTree.push(data);
                }
                else if(c.type.indexOf("Helper")!=-1){
                    //说明是辅助工具
                    helperTree.push(data);
                }

            })
        },
        // findObject(){
        //     this.$bus.$on('emitObject',(object)=>{
        //         if(object==null){
        //             this.Object3D=this.initObject();
        //         }
        //             else{
        //                 this.Object3D=object;
        //                 }
        //         });
        // },
         handleNodeClick(target) {

             if(target!=null&&target.type.indexOf('Camera')==-1&&target.hasOwnProperty('uuid')){
                 //对象不为空：点击场景空白地方
                 //相机不能进行操作
                 //树基本结构标签点击不触发
                 this.current=target.uuid;
                 if(!target.hasOwnProperty('isTrans')&&target.hasOwnProperty('visible')){
                     //说明是场景中拿到的对象
                     this.Object3D=target;
                 }
                 else{
                     //说明是从点击场景树进行的选择物体，需要到editor中拿当前对象
                     //触发一系列查询并修改object事件
                     this.$bus.$emit('requestObj',target);
                     console.log(target)

                 }
             }
            else{
                this.current='';
                this.Object3D=this.initObject();
            }
            this.$bus.$emit('checkEvent',target);
        // if (target.uuid && target.uuid !== this.current.uuid) {
        //     this.current = target;
        //      //console.log(this.current.uuid);
        //     // console.log(target.uuid)

        // }
        // if(target.uuid){
        //     this.$refs.ElTree.setCurrentKey(target.uuid)
        // }

    }
    },
   data() {
       return {
            current:'',
            checked:true,
            Object3D:{
                name:'',
                uuid:'',
                type:'',
                position:new THREE.Vector3(),
                //欧拉角
                rotation:new THREE.Euler(),
                scale:new THREE.Vector3(),
                visible:true,
                color:'#111111',
                isTrans:true
            },
            defaultProps:{

                children: 'children',
                label: 'name'
            },
            sceneStructure: [
                { name: '场景', type: 'base', children: [] },
                { name: '摄像机', type: 'cameraGroup', children: [
                    {
                        name: '透视相机',
                        type:'perspectiveCamera',
                        children:[]
                    }
                ] },
                { name: '光源', type: 'lightGroup', children: [] },
                {name: '辅助工具',type: 'helper',children:[]}
            ],
       }
   },

}
</script>

<style lang="scss" scoped>
.structure {
    height:530px;
    width: 100%;
     overflow-y : auto;
    background: #281928;
    ::v-deep .el-tree {
        background: unset;
        border-bottom: 4px solid #432d43;
        .el-tree-node {
            .el-tree-node__content {
                background: unset;
                &:hover {
                    background: #ffa7fb;
                }
                .el-tree-node__label {
                    color: white;
                    font-size: 10px;
                }
            }
            .el-tree-node__children {
                background: #281928;
            }
        }
        .is-current {
            > .el-tree-node__content {
                background: violet !important;
            }
        }
    }
    .object{
        .Panel{
            border-top: 0px;
            padding-top: 20px;
            display: block;
            border-bottom: 4px solid #432d43;
            .Row{
                    display: flex;
                    align-items: center;
                    min-height: 24px;
                    margin-bottom: 10px;
                .UUIDInput{
                    padding: 2px;
                    border: 1px solid transparent;
                    border-radius: 30px;
                     color: violet!important;
                    width: 150px;
                    font-size: 12px;
                }
                .NameInput{
                    padding: 2px;
                    border: 1px solid transparent;
                    width: 150px;
                    border-radius: 30px;
                    font-size: 12px;
                }
                .Number{
                    cursor: ns-resize;
                    background-color: transparent;
                    width: 50px;
                    color: violet!important;
                    border-radius: 30px;
                    font-size: 10px;
                }
                .Text{
                    cursor: default;
                    display: inline-block;
                    text-indent:20px;
                    width: 90px;
                    color: white;
                    font-size: 10px;
                }
            }

        }

    }
}
</style>
<style lang="scss">
    .el-checkbox__input.is-checked .el-checkbox__inner, .el-checkbox__input.is-indeterminate
    .el-checkbox__inner{
        background-color:violet;
        border-color:violet;
    }
    .el-checkbox__input.is-checked + .el-checkbox__label {
        color: violet;
    }
    .el-checkbox.is-bordered.is-checked{
        border-color: violet;
    }
    .el-checkbox__input.is-focus .el-checkbox__inner{
        border-color:  violet;
    }
</style>
