package doext.implement;

import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View;
import core.helper.DoUIModuleHelper;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoIUIModuleView;
import core.object.DoInvokeResult;
import core.object.DoUIModule;
import doext.define.do_GestureView_IMethod;
import doext.define.do_GestureView_MAbstract;

/**
 * 自定义扩展UIView组件实现类，此类必须继承相应VIEW类，并实现DoIUIModuleView,do_GestureView_IMethod接口；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.model.getUniqueKey());
 */
@SuppressLint("ClickableViewAccessibility")
public class do_GestureView_View extends View implements DoIUIModuleView, do_GestureView_IMethod, OnGestureListener, OnTouchListener {

	/**
	 * 每个UIview都会引用一个具体的model实例；
	 */
	private do_GestureView_MAbstract model;

	private GestureDetector detector;
	private Context mContext;

	public do_GestureView_View(Context context) {
		super(context);
		this.mContext = context;
	}

	/**
	 * 初始化加载view准备,_doUIModule是对应当前UIView的model实例
	 */

	@Override
	public void loadView(DoUIModule _doUIModule) throws Exception {
		this.model = (do_GestureView_MAbstract) _doUIModule;
		// 创建手势检测器
		detector = new GestureDetector(this.mContext, this);
		this.setOnTouchListener(this);
		setClickable(true);
	}

	/**
	 * 动态修改属性值时会被调用，方法返回值为true表示赋值有效，并执行onPropertiesChanged，否则不进行赋值；
	 * 
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public boolean onPropertiesChanging(Map<String, String> _changedValues) {
		return true;
	}

	/**
	 * 属性赋值成功后被调用，可以根据组件定义相关属性值修改UIView可视化操作；
	 * 
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public void onPropertiesChanged(Map<String, String> _changedValues) {
		DoUIModuleHelper.handleBasicViewProperChanged(this.model, _changedValues);
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		return false;
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.model.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) {
		return false;
	}

	/**
	 * 释放资源处理，前端JS脚本调用closePage或执行removeui时会被调用；
	 */
	@Override
	public void onDispose() {
	}

	/**
	 * 重绘组件，构造组件时由系统框架自动调用；
	 * 或者由前端JS脚本调用组件onRedraw方法时被调用（注：通常是需要动态改变组件（X、Y、Width、Height）属性时手动调用）
	 */
	@Override
	public void onRedraw() {
		this.setLayoutParams(DoUIModuleHelper.getLayoutParams(this.model));
	}

	/**
	 * 获取当前model实例
	 */
	@Override
	public DoUIModule getModel() {
		return model;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			fireEvent("touchUp", getResult(event.getX(), event.getY()));
		}
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		fireEvent("touchDown", getResult(e.getX(), e.getY()));
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		fireEvent("touch", getResult(e.getX(), e.getY()));
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		fireEvent("move", getResult(e2.getX(), e2.getY()));
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		fireEvent("longTouch", getResult(e.getX(), e.getY()));
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		fireEvent("fling", getResult("velocityX", velocityX, "velocityY", velocityY));
		return false;
	}

	//除了fling 事件，其他都需要除以zoom
	private DoInvokeResult getResult(float _x, float _y) {
		return getResult("x", _x / model.getXZoom(), "y", _y / model.getYZoom());
	}

	private DoInvokeResult getResult(String _xName, double _x, String _yName, double _y) {
		DoInvokeResult _result = new DoInvokeResult(model.getUniqueKey());
		try {
			JSONObject _obj = new JSONObject();
			_obj.put(_xName, _x);
			_obj.put(_yName, _y);
			_result.setResultNode(_obj);
		} catch (Exception e) {
		}
		return _result;
	}

	private void fireEvent(String _eventName, DoInvokeResult _result) {
		if (_result == null) {
			_result = new DoInvokeResult(model.getUniqueKey());
		}
		model.getEventCenter().fireEvent(_eventName, _result);
	}
}