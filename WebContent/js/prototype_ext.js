Class.thisify = function(me, hash) {
	$H(hash).each(function(pair) { if (Object.isFunction(pair.value)) { hash[pair.key] = pair.value.bind(me); } });
};

Element.addMethods({
	removeChildren: function(element) {
		element = $(element);
		while (element && element.childNodes && element.childNodes.length > 0) {
			if (Object.isElement(element.childNodes[0]))
				$(element.childNodes[0]).removeChildren();
			element.removeChild(element.childNodes[0]);
		}
		return element;
	},
	overlap: function(element1, element2) {
		element1 = $(element1);
		element2 = $(element2);
		var medim = element1.getDimensions();
		var meco = element1.cumulativeOffset();
		var itdim = element2.getDimensions();
		var itco = element2.cumulativeOffset();

		/*
		console.log(itco.left + ', ' + itdim.width + ', ' + itco.top + ', ' + itdim.height);
		console.log(meco.left + ', ' + medim.width + ', ' + meco.top + ', ' + medim.height);		
		console.log(
				(itco.left + itdim.width > meco.left) + ', ' +
				(itco.left < meco.left + medim.width) + ', ' +
				(itco.top + itdim.height > meco.top) + ', ' +
				(itco.top < meco.top + medim.height));
				*/
		return (
				(itco.left + itdim.width > meco.left) &&
				(itco.left < meco.left + medim.width) &&
				(itco.top + itdim.height > meco.top) &&
				(itco.top < meco.top + medim.height));
	},
	setWidth: function(element, w) {
	   	$(element).style.width = w + 'px';
	},
	setHeight: function(element, h) {
   		$(element).style.height = h + 'px';
	},
	setTop: function(element, t) {
	   	$(element).style.top = t + 'px';
	},
	setLeft: function(element, l) {
		$(element).style.left = l + 'px';
	},
	setVisible: function(element) {
		$(element).style.visibility = 'visible';
	},
	setInvisible: function(element) {
		$(element).style.visibility = 'hidden';
	}
});

Element.addMethods('BUTTON', Object.clone(Form.Element.Methods));

var MessageFormat = {
	format: function(s) {
	    var i=1;
	    while (i < arguments.length)
			s = s.replace("{" + (i - 1) + "}", arguments[i++]);
	    return s;
	}
};

Object.extend(Object, {
	asArray: function(a) {
		if (Object.isArray(a))
			return a;
		return [ a ];
	}
});

Object.extend(String.prototype, {
	ltrim: function () {
		for (var i = 0; this.charAt(i) == ' ' || this.charAt(i) == '\t' || this.charAt(i) == '\n'; i++);
		return this.substring(i, this.length);
	},
	rtrim: function() {
		for (var i = this.length - 1; this.charAt(i) == ' ' || this.charAt(i) == '\t' || this.charAt(i) == '\n'; i--);
		return this.substring(0, i + 1);
	},
	trim: function() {
		return this.ltrim().rtrim();
	},
	ltrimall: function() {
		for (var i = 0; this.charAt(i) == ' ' || this.charAt(i) == '\n' || this.charAt(i) == '\t' || this.charAt(i) == '\r'; i++);
		return this.substring(i, this.length);
	},
	rtrimall: function() {
		for (var i = this.length - 1; this.charAt(i) == ' ' || this.charAt(i) == '\n' || this.charAt(i) == '\t' || this.charAt(i) == '\r'; i--);
		return this.substring(0, i + 1);
	},
	trimall: function() {
		return this.ltrimall().rtrimall();
	},
	isInt: function() {
		if (this == '')
			return false;
		if (parseInt(this) != (this * 1))
			return false;
		return true;
	},
	isDouble: function() {
		if (this == '')
			return false;
		if (parseFloat(this, 10) != (this * 1))
			return false;
		return true;
	},
	toDouble: function() {
		return parseFloat(this, 10);
	},
	toInt: function() {
		return parseInt(this);
	}
});

Form.Field = {
	elements: function(name) {
		if (Object.isElement(name))
			return [ name ];
		return Form.Field.NamedCache.returnOrSet(name, function(name) {
			//TODO: this would probably be sped up by replacing the selector with a loop through
			// elements and checking .name attributes and tagName
			var ea = [];
			Object.asArray(name).each(function(name) {
				ea = ea.concat($$(['input[name=' + name + ']','select[name=' + name + ']','textarea[name=' + name + ']','button[name=' + name + ']']));
			});
			return ea;
		});
	},
	firstElement: function(name) {
		var ea = Form.Field.elements(name);
		if (ea.length() == 0)
			return null;
		return ea[0];
	},
	getValue: function(name) {
		return Form.serializeElements(Form.Field.elements(name), {hash: true})[name];
	},
	observe: function(name, eventName, handler) {
		Form.Field.elements(name).each(function(e) { $(e).observe(eventName, handler); });
	},
	stopObserving: function(name, eventName, handler) {
		Form.Field.elements(name).each(function(e) { $(e).stopObserving(eventName, handler); });
	},
	enable: function(name) {
		Form.Field.elements(name).each(Form.Element.Methods.enable);
	},
	disable: function(name) {
		Form.Field.elements(name).each(Form.Element.Methods.disable);
	},
	setValue: function(name, value) {
		Form.Field.elements(name).each(function(e) {
			if (Form.Field.Setters[e.tagName.toLowerCase()])
				Form.Field.Setters[e.tagName.toLowerCase()](e, value);
			else
				Form.Element.setValue(e, value);
			if (e.tagName.toLowerCase() == 'input' && e.readAttribute('type').toLowerCase() == 'hidden')
				e.fire('xp:changed');
		});
	}
}

$FF = Form.Field.getValue;

Form.Field.Setters = {
	input: function(element, value) {
		switch (element.type.toLowerCase()) {
			case 'checkbox':
			case 'radio':
				Form.Element.Serializers.inputSelector(element, value == element.value || value == true);
				break;
			default:
				Form.Element.Serializers.textarea(element, value);
		}
	}
};

// OK, this is a bit nasty, speeds things up but user must be aware of it and
// clear it whenever relevant changes to the DOM are made.
Form.Field.NamedCache = {
	enabled: true,
	cache: new Hash(),
	toKey: function(e) {
		if (Object.isArray(e))
		    return '[' + e.join(', ') + ']';
		return e.toString();
	},
	returnOrSet: function(name, f) {
		if (!this.enabled)
			return f(name);
		var key = this.toKey(name);
		var v = this.cache.get(key);
		if (!v) {
			v = f(name);
			this.cache.set(key, v);
		}
		return v;
	},
	clear: function() {
		this.cache = new Hash();
	},
	disable: function() {
		this.enabled = false;
	},
	enable: function() {
		this.enabled = true;
	}
};

Ajax.Responders.register({
	onCreate: Form.Field.NamedCache.clear.bind(Form.Field.NamedCache)
});

Form.Field.Action = Class.create({
	initialize: function(parent, options, customOptions) {
		this.parent = parent;
		this.options = options || { };
		Object.extend(this.options, customOptions || {});
		this._eventListener = this.eventHappened.bindAsEventListener(this);
		if (this.options.listen) {
			if (Object.isArray(this.options.listen))
				this.options.listen.each(this._listen.bind(this));
			else
				this._listen(this.options.listen);
		}
		this.validatorLoadedListener = this.validatorLoaded.bindAsEventListener(this)
		document.observe('xp:validatorLoaded', this.validatorLoadedListener);
	},
	validatorLoaded: function() {},
	dispose: function() {
		if (this.options.listen) {
			if (Object.isArray(this.options.listen))
				this.options.listen.each(this._unlisten.bind(this));
			else
				this._unlisten(this.options.listen);
		}
		document.stopObserving('xp:validatorLoaded', this.validatorLoadedListener);
	},
	_listen: function(l) {
		Form.Field.observe(l.element, l.type, this._eventListener);
	},
	_unlisten: function(l) {
		Form.Field.stopObserving(l.element, l.type, this._eventListener);
	},
	eventHappened: function(e) { }
});

// does this work? gotta test it
//$w('focus select').each(function(f) { Form.Field[f] = function(name) { Form.Element[f](Form.Field.firstElement(name)) } });

/**** script.aculo.us extensions ****/

/** Effect.Highlight that works for TRs as well (IE doesn't do it properly) **/
document.observe('dom:loaded', function() {
	if (typeof(Effect) != 'undefined' && typeof(Effect.Highlight) != 'undefined') {
		Effect.__Highlight = Effect.Highlight;
		Effect.Highlight = Class.create(Effect.__Highlight, {
			setup: function($super) {
				if (this.element.getStyle('display')=='none') { this.cancel(); return; }
				$super();
				this.cellOldStyles = [];
				if (this.element.tagName == 'TR' && !this.options.keepBackgroundImage) {
					this.element.childElements().each((function(td) {
						this.cellOldStyles.push({ backgroundImage: td.getStyle('background-image') });
						td.setStyle({backgroundImage: 'none'});
					}).bind(this));
				}			
			},
			update: function(position) {
				this.applyColour(this.element, $R(0,2).inject('#', function(m,v,i) {
					return m+(Math.round(this._base[i]+(this._delta[i]*position)).toColorPart());
				}.bind(this)));
			},
			finish: function() {
				if (this.element.tagName == 'TR') {
					var tds = this.element.childElements();
					for (var i = 0; i < tds.length && i < this.cellOldStyles.length; i++)
						tds[i].setStyle(Object.extend(this.cellOldStyles[i], { backgroundColor: this.options.restorecolor }));
				} else
					this.element.setStyle(Object.extend(this.oldStyle, { backgroundColor: this.options.restorecolor }));
			},
			applyColour: function(element, colour) {
				if (element.tagName == 'TR')
					element.childElements().each((function(td) { this.applyColour(td, colour); }).bind(this));
				else
					element.setStyle({backgroundColor: colour});
			}
		});
	}

	//thanks to lowpro: http://www.danwebb.net/lowpro for the basics of this
	("p|div|span|strong|em|img|table|tr|td|th|thead|tbody|tfoot|pre|code|" + 
			"h1|h2|h3|h4|h5|h6|ul|ol|li|form|input|textarea|legend|fieldset|" + 
			"select|option|blockquote|cite|br|hr|dd|dl|dt|address|a|button|abbr|acronym|" +
			"script|link|style|bdo|ins|del|object|param|col|colgroup|optgroup|caption|" + 
			"label|dfn|kbd|samp|var").split("|").each(
			function(el) {
				window['$' + el] = function() {
					return Element.extend(Builder.node.apply(Builder, [el].concat(Array.prototype.slice.apply(arguments))));
				};
			});
});	

