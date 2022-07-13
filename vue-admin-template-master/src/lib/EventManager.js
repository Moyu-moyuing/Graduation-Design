import { Vector2 } from "three";
import { Raycaster } from "three";
import { EventDispatcher } from "three";
import {Intersection} from "three";

//事件处理全局机制
let _that;
class EventManager extends EventDispatcher{

    constructor(dom,scene,camera,objs){
        super();
        _that=this;
        this.raycaster=new Raycaster();
        this.mouse=new Vector2();
        this.cacheobjects=null;
        this.dom=dom;
        this.scene=scene;
        this.camera=camera;
        this.objs=objs;
        this.dom.addEventListener('mousedown',this.MouseDown);
       this.dom.addEventListener('mouseup',this.MouseUp);

       this.dom.addEventListener('mousemove',this.MouseMove);
        //物体拾取事件处理
        this.dom.addEventListener('click',this.MouseClick);

    }
    MouseDown(event){

        _that.raycaster.setFromCamera(_that.mouse,_that.camera);
        //防止控制器被选中
        // scene.remove(transformControl);
        const intersection=_that.raycaster.intersectObjects(_that.scene.children,false);
        //把所有有children的屏蔽掉，可以防止对helper进行控制
        // scene.add(transformControl);
        _that.dispatchEvent({
          type:'mousedown',
          intersection
        });
        if(intersection.length){
          const object=intersection[0].object;
          object.dispatchEvent({
            type:'mousedown',
          });
          // transformControl.attach(object);
        }

    }
    MouseUp(event){

        _that.raycaster.setFromCamera(_that.mouse,_that.camera);
        //防止控制器被选中
        // scene.remove(transformControl);
        const intersection=_that.raycaster.intersectObjects(_that.scene.children,false);
        //把所有有children的屏蔽掉，可以防止对helper进行控制
        // scene.add(transformControl);
        _that.dispatchEvent({
          type:'mouseup',
          intersection
        });
        if(intersection.length){
          const object=intersection[0].object;
          object.dispatchEvent({
            type:'mouseup',
          });
          // transformControl.attach(object);
        }

    }
    MouseMove(event){

        let newobjs=[];
        if(_that.scene.children.length>0){
          let childobj;
          for(var i=_that.scene.children.length-1;i>=0;i--){
            childobj=_that.scene.children[i];
            if(childobj.type.indexOf('Object3D')!=-1||childobj.type.indexOf('Mesh')!=-1||childobj.type.indexOf('Group')!=-1){
              newobjs.push(childobj);
            }
            if((childobj.type.indexOf('Light')!=-1&&childobj.type.indexOf('Helper')==-1)){
              newobjs.push(childobj);
            }
          }
        }
        //console.log(event.offsetX,event.offsetY);
      _that.mouse.x=(event.offsetX/ _that.dom.offsetWidth)*2-1;
      _that.mouse.y=(-event.offsetY*2)/ _that.dom.offsetHeight+1;
        // console.log(mouse.x,mouse.y);
      _that.raycaster.setFromCamera(_that.mouse,_that.camera);
        //防止控制器被选中
        // scene.remove(transformControl);
        const intersection=_that.raycaster.intersectObjects(newobjs,true);
        //把所有有children的屏蔽掉，可以防止对helper进行控制
        // scene.add(transformControl);
      _that.dispatchEvent({
          type:'mousemove',
          intersection
        });
        if(intersection.length){
          //如果拾取到材质物体
          const object=intersection[0].object;
          //如果当下物体不是上次缓存物体，说明是从不同物体上移到当前物体上触发mouseenter事件
          if(object!=_that.cacheobjects){
            if(_that.cacheobjects){
              //当鼠标从一个物体移动到另一个物体时，先触发之前缓存物体的mouseleave方法
              //再触发当前物体的mouseenter方法
              _that.cacheobjects.dispatchEvent({
                type:'mouseleave'
              });
            }
            object.dispatchEvent({
              type:'mouseenter'
            });
          }//如果相等触发mousemove方法得到物体坐标(鼠标一直在物体上移动)
          else if(object==_that.cacheobjects){
            object.dispatchEvent({
              type:'mousemove'
            });
          }
          _that.cacheobjects=object;
        }else{
          //当鼠标从一个物体移动到射线拾取器拾取不到物体时也会触发之前的物体的mouseleave方法
          if(_that.cacheobjects){
            //当鼠标从一个物体移动到另一个物体时，先触发之前缓存物体的mouseleave方法
            //再触发当前物体的mouseenter方法
            _that.cacheobjects.dispatchEvent({
              type:'mouseleave'
            });
          }
          _that.cacheobjects=null;


        }

    }
    MouseClick(event){

        // if(transing){
        //     transing=false;
        //     return false;
        // }
        let newobjs=[];
        if(_that.scene.children.length>0){
          let childobj;
          for(var i=_that.scene.children.length-1;i>=0;i--){
            childobj=_that.scene.children[i];
            if(childobj.type.indexOf('Object3D')!=-1||childobj.type.indexOf('Mesh')!=-1||childobj.type.indexOf('Group')!=-1){
              newobjs.push(childobj);
            }
            if((childobj.type.indexOf('Light')!=-1&&childobj.type.indexOf('Helper')==-1)){
              newobjs.push(childobj);
            }
          }
        }
        // console.log(newobjs);
      _that.raycaster.setFromCamera(_that.mouse,_that.camera);
        //防止控制器被选中
        // scene.remove(transformControl);
        const intersection=_that.raycaster.intersectObjects(newobjs,true);
        console.log(intersection);
        //把所有有children的屏蔽掉，可以防止对helper进行控制
        // scene.add(transformControl);
      _that.dispatchEvent({
          type:'click',
          intersection
        });
        if(intersection.length){
          const object=intersection[0].object;
          object.dispatchEvent({
            type:'click',
          });
          // transformControl.attach(object);
        }

    }
    RemoveEvent(){
      _that.dom.removeEventListener('click',_that.MouseClick);
      _that.dom.removeEventListener('mousemove',_that.MouseMove);
      _that.dom.removeEventListener('mouseup',_that.MouseUp);
      _that.dom.removeEventListener('mousedown',_that.MouseDown);
    }
}
export {EventManager};
