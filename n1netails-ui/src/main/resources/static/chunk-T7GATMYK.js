import {
  AuthenticationService,
  BaseChartDirective,
  BidiModule,
  CdkPortalOutlet,
  ComponentPortal,
  Directionality,
  HeaderComponent,
  HttpClient,
  NgTemplateOutlet,
  NzAvatarComponent,
  NzAvatarModule,
  NzColDirective,
  NzConfigService,
  NzContentComponent,
  NzDestroyService,
  NzGridModule,
  NzHeaderComponent,
  NzI18nService,
  NzIconDirective,
  NzIconModule,
  NzLayoutComponent,
  NzLayoutModule,
  NzMessageService,
  NzOutletModule,
  NzRowDirective,
  NzSiderComponent,
  NzStringTemplateOutletDirective,
  PortalModule,
  Router,
  SidenavComponent,
  TemplatePortal,
  UiConfigService,
  WithConfig,
  toCssPixel
} from "./chunk-EKTH4PDM.js";
import "./chunk-MMDGBEIF.js";
import {
  BehaviorSubject,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChild,
  ContentChildren,
  Directive,
  ElementRef,
  HostBinding,
  InjectionToken,
  Injector,
  Input,
  NgModule,
  ReplaySubject,
  Subject,
  TemplateRef,
  Type,
  ViewChild,
  ViewContainerRef,
  ViewEncapsulation,
  __esDecorate,
  __runInitializers,
  __spreadValues,
  booleanAttribute,
  catchError,
  debounce,
  defer,
  distinctUntilChanged,
  merge,
  mergeMap,
  numberAttribute,
  of,
  setClassMetadata,
  startWith,
  switchMap,
  takeUntil,
  timer,
  ɵsetClassDebugInfo,
  ɵɵNgOnChangesFeature,
  ɵɵProvidersFeature,
  ɵɵadvance,
  ɵɵclassProp,
  ɵɵconditional,
  ɵɵcontentQuery,
  ɵɵdefineComponent,
  ɵɵdefineDirective,
  ɵɵdefineInjector,
  ɵɵdefineNgModule,
  ɵɵdirectiveInject,
  ɵɵelement,
  ɵɵelementContainer,
  ɵɵelementContainerEnd,
  ɵɵelementContainerStart,
  ɵɵelementEnd,
  ɵɵelementStart,
  ɵɵgetCurrentView,
  ɵɵlistener,
  ɵɵloadQuery,
  ɵɵnamespaceSVG,
  ɵɵnextContext,
  ɵɵprojection,
  ɵɵprojectionDef,
  ɵɵproperty,
  ɵɵpureFunction0,
  ɵɵpureFunction2,
  ɵɵqueryRefresh,
  ɵɵreference,
  ɵɵrepeater,
  ɵɵrepeaterCreate,
  ɵɵrepeaterTrackByIdentity,
  ɵɵrepeaterTrackByIndex,
  ɵɵresetView,
  ɵɵrestoreView,
  ɵɵsanitizeUrl,
  ɵɵstyleMap,
  ɵɵstyleProp,
  ɵɵtemplate,
  ɵɵtemplateRefExtractor,
  ɵɵtext,
  ɵɵtextInterpolate,
  ɵɵtextInterpolate1,
  ɵɵviewQuery
} from "./chunk-446R5MAN.js";

// node_modules/ng-zorro-antd/fesm2022/ng-zorro-antd-skeleton.mjs
var _c0 = ["nzType", "button"];
var _c1 = ["nzType", "avatar"];
var _c2 = ["nzType", "input"];
var _c3 = ["nzType", "image"];
var _c4 = ["*"];
function NzSkeletonComponent_Conditional_0_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 0);
    \u0275\u0275element(1, "nz-skeleton-element", 4);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("nzSize", ctx_r0.avatar.size || "default")("nzShape", ctx_r0.avatar.shape || "circle");
  }
}
function NzSkeletonComponent_Conditional_0_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "h3", 5);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275styleProp("width", ctx_r0.toCSSUnit(ctx_r0.title.width));
  }
}
function NzSkeletonComponent_Conditional_0_Conditional_3_For_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "li");
  }
  if (rf & 2) {
    const \u0275$index_15_r2 = ctx.$index;
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275styleProp("width", ctx_r0.toCSSUnit(ctx_r0.widthList[\u0275$index_15_r2]));
  }
}
function NzSkeletonComponent_Conditional_0_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "ul", 3);
    \u0275\u0275repeaterCreate(1, NzSkeletonComponent_Conditional_0_Conditional_3_For_2_Template, 1, 2, "li", 6, \u0275\u0275repeaterTrackByIdentity);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275repeater(ctx_r0.rowsList);
  }
}
function NzSkeletonComponent_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzSkeletonComponent_Conditional_0_Conditional_0_Template, 2, 2, "div", 0);
    \u0275\u0275elementStart(1, "div", 1);
    \u0275\u0275template(2, NzSkeletonComponent_Conditional_0_Conditional_2_Template, 1, 2, "h3", 2)(3, NzSkeletonComponent_Conditional_0_Conditional_3_Template, 3, 0, "ul", 3);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275conditional(!!ctx_r0.nzAvatar ? 0 : -1);
    \u0275\u0275advance(2);
    \u0275\u0275conditional(!!ctx_r0.nzTitle ? 2 : -1);
    \u0275\u0275advance();
    \u0275\u0275conditional(!!ctx_r0.nzParagraph ? 3 : -1);
  }
}
function NzSkeletonComponent_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275projection(0);
  }
}
var NzSkeletonElementDirective = class _NzSkeletonElementDirective {
  nzActive = false;
  nzType;
  nzBlock = false;
  static \u0275fac = function NzSkeletonElementDirective_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSkeletonElementDirective)();
  };
  static \u0275dir = /* @__PURE__ */ \u0275\u0275defineDirective({
    type: _NzSkeletonElementDirective,
    selectors: [["nz-skeleton-element"]],
    hostAttrs: [1, "ant-skeleton", "ant-skeleton-element"],
    hostVars: 4,
    hostBindings: function NzSkeletonElementDirective_HostBindings(rf, ctx) {
      if (rf & 2) {
        \u0275\u0275classProp("ant-skeleton-active", ctx.nzActive)("ant-skeleton-block", ctx.nzBlock);
      }
    },
    inputs: {
      nzActive: [2, "nzActive", "nzActive", booleanAttribute],
      nzType: "nzType",
      nzBlock: [2, "nzBlock", "nzBlock", booleanAttribute]
    }
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSkeletonElementDirective, [{
    type: Directive,
    args: [{
      selector: "nz-skeleton-element",
      host: {
        class: "ant-skeleton ant-skeleton-element",
        "[class.ant-skeleton-active]": "nzActive",
        "[class.ant-skeleton-block]": "nzBlock"
      }
    }]
  }], null, {
    nzActive: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzType: [{
      type: Input
    }],
    nzBlock: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }]
  });
})();
var NzSkeletonElementButtonComponent = class _NzSkeletonElementButtonComponent {
  nzShape = "default";
  nzSize = "default";
  static \u0275fac = function NzSkeletonElementButtonComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSkeletonElementButtonComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzSkeletonElementButtonComponent,
    selectors: [["nz-skeleton-element", "nzType", "button"]],
    inputs: {
      nzShape: "nzShape",
      nzSize: "nzSize"
    },
    attrs: _c0,
    decls: 1,
    vars: 10,
    consts: [[1, "ant-skeleton-button"]],
    template: function NzSkeletonElementButtonComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275element(0, "span", 0);
      }
      if (rf & 2) {
        \u0275\u0275classProp("ant-skeleton-button-square", ctx.nzShape === "square")("ant-skeleton-button-round", ctx.nzShape === "round")("ant-skeleton-button-circle", ctx.nzShape === "circle")("ant-skeleton-button-lg", ctx.nzSize === "large")("ant-skeleton-button-sm", ctx.nzSize === "small");
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSkeletonElementButtonComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      selector: 'nz-skeleton-element[nzType="button"]',
      template: `
    <span
      class="ant-skeleton-button"
      [class.ant-skeleton-button-square]="nzShape === 'square'"
      [class.ant-skeleton-button-round]="nzShape === 'round'"
      [class.ant-skeleton-button-circle]="nzShape === 'circle'"
      [class.ant-skeleton-button-lg]="nzSize === 'large'"
      [class.ant-skeleton-button-sm]="nzSize === 'small'"
    ></span>
  `
    }]
  }], null, {
    nzShape: [{
      type: Input
    }],
    nzSize: [{
      type: Input
    }]
  });
})();
var NzSkeletonElementAvatarComponent = class _NzSkeletonElementAvatarComponent {
  nzShape = "circle";
  nzSize = "default";
  styleMap = {};
  ngOnChanges(changes) {
    if (changes.nzSize && typeof this.nzSize === "number") {
      const sideLength = `${this.nzSize}px`;
      this.styleMap = {
        width: sideLength,
        height: sideLength,
        "line-height": sideLength
      };
    } else {
      this.styleMap = {};
    }
  }
  static \u0275fac = function NzSkeletonElementAvatarComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSkeletonElementAvatarComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzSkeletonElementAvatarComponent,
    selectors: [["nz-skeleton-element", "nzType", "avatar"]],
    inputs: {
      nzShape: "nzShape",
      nzSize: "nzSize"
    },
    features: [\u0275\u0275NgOnChangesFeature],
    attrs: _c1,
    decls: 1,
    vars: 10,
    consts: [[1, "ant-skeleton-avatar"]],
    template: function NzSkeletonElementAvatarComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275element(0, "span", 0);
      }
      if (rf & 2) {
        \u0275\u0275styleMap(ctx.styleMap);
        \u0275\u0275classProp("ant-skeleton-avatar-square", ctx.nzShape === "square")("ant-skeleton-avatar-circle", ctx.nzShape === "circle")("ant-skeleton-avatar-lg", ctx.nzSize === "large")("ant-skeleton-avatar-sm", ctx.nzSize === "small");
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSkeletonElementAvatarComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      selector: 'nz-skeleton-element[nzType="avatar"]',
      template: `
    <span
      class="ant-skeleton-avatar"
      [class.ant-skeleton-avatar-square]="nzShape === 'square'"
      [class.ant-skeleton-avatar-circle]="nzShape === 'circle'"
      [class.ant-skeleton-avatar-lg]="nzSize === 'large'"
      [class.ant-skeleton-avatar-sm]="nzSize === 'small'"
      [style]="styleMap"
    ></span>
  `
    }]
  }], null, {
    nzShape: [{
      type: Input
    }],
    nzSize: [{
      type: Input
    }]
  });
})();
var NzSkeletonElementInputComponent = class _NzSkeletonElementInputComponent {
  nzSize = "default";
  static \u0275fac = function NzSkeletonElementInputComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSkeletonElementInputComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzSkeletonElementInputComponent,
    selectors: [["nz-skeleton-element", "nzType", "input"]],
    inputs: {
      nzSize: "nzSize"
    },
    attrs: _c2,
    decls: 1,
    vars: 4,
    consts: [[1, "ant-skeleton-input"]],
    template: function NzSkeletonElementInputComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275element(0, "span", 0);
      }
      if (rf & 2) {
        \u0275\u0275classProp("ant-skeleton-input-lg", ctx.nzSize === "large")("ant-skeleton-input-sm", ctx.nzSize === "small");
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSkeletonElementInputComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      selector: 'nz-skeleton-element[nzType="input"]',
      template: `
    <span
      class="ant-skeleton-input"
      [class.ant-skeleton-input-lg]="nzSize === 'large'"
      [class.ant-skeleton-input-sm]="nzSize === 'small'"
    ></span>
  `
    }]
  }], null, {
    nzSize: [{
      type: Input
    }]
  });
})();
var NzSkeletonElementImageComponent = class _NzSkeletonElementImageComponent {
  static \u0275fac = function NzSkeletonElementImageComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSkeletonElementImageComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzSkeletonElementImageComponent,
    selectors: [["nz-skeleton-element", "nzType", "image"]],
    attrs: _c3,
    decls: 3,
    vars: 0,
    consts: [[1, "ant-skeleton-image"], ["viewBox", "0 0 1098 1024", "xmlns", "http://www.w3.org/2000/svg", 1, "ant-skeleton-image-svg"], ["d", "M365.714286 329.142857q0 45.714286-32.036571 77.677714t-77.677714 32.036571-77.677714-32.036571-32.036571-77.677714 32.036571-77.677714 77.677714-32.036571 77.677714 32.036571 32.036571 77.677714zM950.857143 548.571429l0 256-804.571429 0 0-109.714286 182.857143-182.857143 91.428571 91.428571 292.571429-292.571429zM1005.714286 146.285714l-914.285714 0q-7.460571 0-12.873143 5.412571t-5.412571 12.873143l0 694.857143q0 7.460571 5.412571 12.873143t12.873143 5.412571l914.285714 0q7.460571 0 12.873143-5.412571t5.412571-12.873143l0-694.857143q0-7.460571-5.412571-12.873143t-12.873143-5.412571zM1097.142857 164.571429l0 694.857143q0 37.741714-26.843429 64.585143t-64.585143 26.843429l-914.285714 0q-37.741714 0-64.585143-26.843429t-26.843429-64.585143l0-694.857143q0-37.741714 26.843429-64.585143t64.585143-26.843429l914.285714 0q37.741714 0 64.585143 26.843429t26.843429 64.585143z", 1, "ant-skeleton-image-path"]],
    template: function NzSkeletonElementImageComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275elementStart(0, "span", 0);
        \u0275\u0275namespaceSVG();
        \u0275\u0275elementStart(1, "svg", 1);
        \u0275\u0275element(2, "path", 2);
        \u0275\u0275elementEnd()();
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSkeletonElementImageComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      selector: 'nz-skeleton-element[nzType="image"]',
      template: `
    <span class="ant-skeleton-image">
      <svg class="ant-skeleton-image-svg" viewBox="0 0 1098 1024" xmlns="http://www.w3.org/2000/svg">
        <path
          d="M365.714286 329.142857q0 45.714286-32.036571 77.677714t-77.677714 32.036571-77.677714-32.036571-32.036571-77.677714 32.036571-77.677714 77.677714-32.036571 77.677714 32.036571 32.036571 77.677714zM950.857143 548.571429l0 256-804.571429 0 0-109.714286 182.857143-182.857143 91.428571 91.428571 292.571429-292.571429zM1005.714286 146.285714l-914.285714 0q-7.460571 0-12.873143 5.412571t-5.412571 12.873143l0 694.857143q0 7.460571 5.412571 12.873143t12.873143 5.412571l914.285714 0q7.460571 0 12.873143-5.412571t5.412571-12.873143l0-694.857143q0-7.460571-5.412571-12.873143t-12.873143-5.412571zM1097.142857 164.571429l0 694.857143q0 37.741714-26.843429 64.585143t-64.585143 26.843429l-914.285714 0q-37.741714 0-64.585143-26.843429t-26.843429-64.585143l0-694.857143q0-37.741714 26.843429-64.585143t64.585143-26.843429l914.285714 0q37.741714 0 64.585143 26.843429t26.843429 64.585143z"
          class="ant-skeleton-image-path"
        />
      </svg>
    </span>
  `
    }]
  }], null, null);
})();
var NzSkeletonComponent = class _NzSkeletonComponent {
  cdr;
  nzActive = false;
  nzLoading = true;
  nzRound = false;
  nzTitle = true;
  nzAvatar = false;
  nzParagraph = true;
  title;
  avatar;
  paragraph;
  rowsList = [];
  widthList = [];
  constructor(cdr) {
    this.cdr = cdr;
  }
  toCSSUnit(value = "") {
    return toCssPixel(value);
  }
  getTitleProps() {
    const hasAvatar = !!this.nzAvatar;
    const hasParagraph = !!this.nzParagraph;
    let width = "";
    if (!hasAvatar && hasParagraph) {
      width = "38%";
    } else if (hasAvatar && hasParagraph) {
      width = "50%";
    }
    return __spreadValues({
      width
    }, this.getProps(this.nzTitle));
  }
  getAvatarProps() {
    const shape = !!this.nzTitle && !this.nzParagraph ? "square" : "circle";
    const size = "large";
    return __spreadValues({
      shape,
      size
    }, this.getProps(this.nzAvatar));
  }
  getParagraphProps() {
    const hasAvatar = !!this.nzAvatar;
    const hasTitle = !!this.nzTitle;
    const basicProps = {};
    if (!hasAvatar || !hasTitle) {
      basicProps.width = "61%";
    }
    if (!hasAvatar && hasTitle) {
      basicProps.rows = 3;
    } else {
      basicProps.rows = 2;
    }
    return __spreadValues(__spreadValues({}, basicProps), this.getProps(this.nzParagraph));
  }
  getProps(prop) {
    return prop && typeof prop === "object" ? prop : {};
  }
  getWidthList() {
    const {
      width,
      rows
    } = this.paragraph;
    let widthList = [];
    if (width && Array.isArray(width)) {
      widthList = width;
    } else if (width && !Array.isArray(width)) {
      widthList = [];
      widthList[rows - 1] = width;
    }
    return widthList;
  }
  updateProps() {
    this.title = this.getTitleProps();
    this.avatar = this.getAvatarProps();
    this.paragraph = this.getParagraphProps();
    this.rowsList = [...Array(this.paragraph.rows)];
    this.widthList = this.getWidthList();
    this.cdr.markForCheck();
  }
  ngOnInit() {
    this.updateProps();
  }
  ngOnChanges(changes) {
    if (changes.nzTitle || changes.nzAvatar || changes.nzParagraph) {
      this.updateProps();
    }
  }
  static \u0275fac = function NzSkeletonComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSkeletonComponent)(\u0275\u0275directiveInject(ChangeDetectorRef));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzSkeletonComponent,
    selectors: [["nz-skeleton"]],
    hostAttrs: [1, "ant-skeleton"],
    hostVars: 6,
    hostBindings: function NzSkeletonComponent_HostBindings(rf, ctx) {
      if (rf & 2) {
        \u0275\u0275classProp("ant-skeleton-with-avatar", !!ctx.nzAvatar)("ant-skeleton-active", ctx.nzActive)("ant-skeleton-round", !!ctx.nzRound);
      }
    },
    inputs: {
      nzActive: "nzActive",
      nzLoading: "nzLoading",
      nzRound: "nzRound",
      nzTitle: "nzTitle",
      nzAvatar: "nzAvatar",
      nzParagraph: "nzParagraph"
    },
    exportAs: ["nzSkeleton"],
    features: [\u0275\u0275NgOnChangesFeature],
    ngContentSelectors: _c4,
    decls: 2,
    vars: 1,
    consts: [[1, "ant-skeleton-header"], [1, "ant-skeleton-content"], [1, "ant-skeleton-title", 3, "width"], [1, "ant-skeleton-paragraph"], ["nzType", "avatar", 3, "nzSize", "nzShape"], [1, "ant-skeleton-title"], [3, "width"]],
    template: function NzSkeletonComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275template(0, NzSkeletonComponent_Conditional_0_Template, 4, 3)(1, NzSkeletonComponent_Conditional_1_Template, 1, 0);
      }
      if (rf & 2) {
        \u0275\u0275conditional(ctx.nzLoading ? 0 : 1);
      }
    },
    dependencies: [NzSkeletonElementDirective, NzSkeletonElementAvatarComponent],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSkeletonComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      selector: "nz-skeleton",
      exportAs: "nzSkeleton",
      host: {
        class: "ant-skeleton",
        "[class.ant-skeleton-with-avatar]": "!!nzAvatar",
        "[class.ant-skeleton-active]": "nzActive",
        "[class.ant-skeleton-round]": "!!nzRound"
      },
      template: `
    @if (nzLoading) {
      @if (!!nzAvatar) {
        <div class="ant-skeleton-header">
          <nz-skeleton-element
            nzType="avatar"
            [nzSize]="avatar.size || 'default'"
            [nzShape]="avatar.shape || 'circle'"
          ></nz-skeleton-element>
        </div>
      }
      <div class="ant-skeleton-content">
        @if (!!nzTitle) {
          <h3 class="ant-skeleton-title" [style.width]="toCSSUnit(title.width)"></h3>
        }
        @if (!!nzParagraph) {
          <ul class="ant-skeleton-paragraph">
            @for (row of rowsList; track row; let i = $index) {
              <li [style.width]="toCSSUnit(widthList[i])"></li>
            }
          </ul>
        }
      </div>
    } @else {
      <ng-content></ng-content>
    }
  `,
      imports: [NzSkeletonElementDirective, NzSkeletonElementAvatarComponent]
    }]
  }], () => [{
    type: ChangeDetectorRef
  }], {
    nzActive: [{
      type: Input
    }],
    nzLoading: [{
      type: Input
    }],
    nzRound: [{
      type: Input
    }],
    nzTitle: [{
      type: Input
    }],
    nzAvatar: [{
      type: Input
    }],
    nzParagraph: [{
      type: Input
    }]
  });
})();
var NzSkeletonModule = class _NzSkeletonModule {
  static \u0275fac = function NzSkeletonModule_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSkeletonModule)();
  };
  static \u0275mod = /* @__PURE__ */ \u0275\u0275defineNgModule({
    type: _NzSkeletonModule
  });
  static \u0275inj = /* @__PURE__ */ \u0275\u0275defineInjector({});
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSkeletonModule, [{
    type: NgModule,
    args: [{
      imports: [NzSkeletonElementDirective, NzSkeletonComponent, NzSkeletonElementButtonComponent, NzSkeletonElementAvatarComponent, NzSkeletonElementImageComponent, NzSkeletonElementInputComponent],
      exports: [NzSkeletonElementDirective, NzSkeletonComponent, NzSkeletonElementButtonComponent, NzSkeletonElementAvatarComponent, NzSkeletonElementImageComponent, NzSkeletonElementInputComponent]
    }]
  }], null, null);
})();

// node_modules/ng-zorro-antd/fesm2022/ng-zorro-antd-card.mjs
function NzCardMetaComponent_Conditional_0_ng_template_1_Template(rf, ctx) {
}
function NzCardMetaComponent_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 0);
    \u0275\u0275template(1, NzCardMetaComponent_Conditional_0_ng_template_1_Template, 0, 0, "ng-template", 2);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzAvatar);
  }
}
function NzCardMetaComponent_Conditional_1_Conditional_1_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzTitle);
  }
}
function NzCardMetaComponent_Conditional_1_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 3);
    \u0275\u0275template(1, NzCardMetaComponent_Conditional_1_Conditional_1_ng_container_1_Template, 2, 1, "ng-container", 5);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzTitle);
  }
}
function NzCardMetaComponent_Conditional_1_Conditional_2_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzDescription);
  }
}
function NzCardMetaComponent_Conditional_1_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 4);
    \u0275\u0275template(1, NzCardMetaComponent_Conditional_1_Conditional_2_ng_container_1_Template, 2, 1, "ng-container", 5);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzDescription);
  }
}
function NzCardMetaComponent_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 1);
    \u0275\u0275template(1, NzCardMetaComponent_Conditional_1_Conditional_1_Template, 2, 1, "div", 3)(2, NzCardMetaComponent_Conditional_1_Conditional_2_Template, 2, 1, "div", 4);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.nzTitle ? 1 : -1);
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.nzDescription ? 2 : -1);
  }
}
var _c02 = ["*"];
function NzCardTabComponent_ng_template_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275projection(0);
  }
}
var _c12 = () => ({
  rows: 4
});
function NzCardComponent_Conditional_0_Conditional_2_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzTitle);
  }
}
function NzCardComponent_Conditional_0_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 6);
    \u0275\u0275template(1, NzCardComponent_Conditional_0_Conditional_2_ng_container_1_Template, 2, 1, "ng-container", 9);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzTitle);
  }
}
function NzCardComponent_Conditional_0_Conditional_3_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzExtra);
  }
}
function NzCardComponent_Conditional_0_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 7);
    \u0275\u0275template(1, NzCardComponent_Conditional_0_Conditional_3_ng_container_1_Template, 2, 1, "ng-container", 9);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzExtra);
  }
}
function NzCardComponent_Conditional_0_Conditional_4_ng_template_0_Template(rf, ctx) {
}
function NzCardComponent_Conditional_0_Conditional_4_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzCardComponent_Conditional_0_Conditional_4_ng_template_0_Template, 0, 0, "ng-template", 8);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.listOfNzCardTabComponent.template);
  }
}
function NzCardComponent_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 0)(1, "div", 5);
    \u0275\u0275template(2, NzCardComponent_Conditional_0_Conditional_2_Template, 2, 1, "div", 6)(3, NzCardComponent_Conditional_0_Conditional_3_Template, 2, 1, "div", 7);
    \u0275\u0275elementEnd();
    \u0275\u0275template(4, NzCardComponent_Conditional_0_Conditional_4_Template, 1, 1, null, 8);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance(2);
    \u0275\u0275conditional(ctx_r0.nzTitle ? 2 : -1);
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.nzExtra ? 3 : -1);
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.listOfNzCardTabComponent ? 4 : -1);
  }
}
function NzCardComponent_Conditional_1_ng_template_1_Template(rf, ctx) {
}
function NzCardComponent_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 1);
    \u0275\u0275template(1, NzCardComponent_Conditional_1_ng_template_1_Template, 0, 0, "ng-template", 8);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzCover);
  }
}
function NzCardComponent_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-skeleton", 3);
  }
  if (rf & 2) {
    \u0275\u0275property("nzActive", true)("nzTitle", false)("nzParagraph", \u0275\u0275pureFunction0(3, _c12));
  }
}
function NzCardComponent_Conditional_4_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275projection(0);
  }
}
function NzCardComponent_Conditional_5_For_2_ng_template_2_Template(rf, ctx) {
}
function NzCardComponent_Conditional_5_For_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "li")(1, "span");
    \u0275\u0275template(2, NzCardComponent_Conditional_5_For_2_ng_template_2_Template, 0, 0, "ng-template", 8);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const action_r2 = ctx.$implicit;
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275styleProp("width", 100 / ctx_r0.nzActions.length, "%");
    \u0275\u0275advance(2);
    \u0275\u0275property("ngTemplateOutlet", action_r2);
  }
}
function NzCardComponent_Conditional_5_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "ul", 4);
    \u0275\u0275repeaterCreate(1, NzCardComponent_Conditional_5_For_2_Template, 3, 3, "li", 10, \u0275\u0275repeaterTrackByIndex);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275repeater(ctx_r0.nzActions);
  }
}
var NzCardGridDirective = class _NzCardGridDirective {
  nzHoverable = true;
  static \u0275fac = function NzCardGridDirective_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzCardGridDirective)();
  };
  static \u0275dir = /* @__PURE__ */ \u0275\u0275defineDirective({
    type: _NzCardGridDirective,
    selectors: [["", "nz-card-grid", ""]],
    hostAttrs: [1, "ant-card-grid"],
    hostVars: 2,
    hostBindings: function NzCardGridDirective_HostBindings(rf, ctx) {
      if (rf & 2) {
        \u0275\u0275classProp("ant-card-hoverable", ctx.nzHoverable);
      }
    },
    inputs: {
      nzHoverable: [2, "nzHoverable", "nzHoverable", booleanAttribute]
    },
    exportAs: ["nzCardGrid"]
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzCardGridDirective, [{
    type: Directive,
    args: [{
      selector: "[nz-card-grid]",
      exportAs: "nzCardGrid",
      host: {
        class: "ant-card-grid",
        "[class.ant-card-hoverable]": "nzHoverable"
      }
    }]
  }], null, {
    nzHoverable: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }]
  });
})();
var NzCardMetaComponent = class _NzCardMetaComponent {
  nzTitle = null;
  nzDescription = null;
  nzAvatar = null;
  static \u0275fac = function NzCardMetaComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzCardMetaComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzCardMetaComponent,
    selectors: [["nz-card-meta"]],
    hostAttrs: [1, "ant-card-meta"],
    inputs: {
      nzTitle: "nzTitle",
      nzDescription: "nzDescription",
      nzAvatar: "nzAvatar"
    },
    exportAs: ["nzCardMeta"],
    decls: 2,
    vars: 2,
    consts: [[1, "ant-card-meta-avatar"], [1, "ant-card-meta-detail"], [3, "ngTemplateOutlet"], [1, "ant-card-meta-title"], [1, "ant-card-meta-description"], [4, "nzStringTemplateOutlet"]],
    template: function NzCardMetaComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275template(0, NzCardMetaComponent_Conditional_0_Template, 2, 1, "div", 0)(1, NzCardMetaComponent_Conditional_1_Template, 3, 2, "div", 1);
      }
      if (rf & 2) {
        \u0275\u0275conditional(ctx.nzAvatar ? 0 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(ctx.nzTitle || ctx.nzDescription ? 1 : -1);
      }
    },
    dependencies: [NgTemplateOutlet, NzOutletModule, NzStringTemplateOutletDirective],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzCardMetaComponent, [{
    type: Component,
    args: [{
      selector: "nz-card-meta",
      exportAs: "nzCardMeta",
      preserveWhitespaces: false,
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      template: `
    @if (nzAvatar) {
      <div class="ant-card-meta-avatar">
        <ng-template [ngTemplateOutlet]="nzAvatar" />
      </div>
    }

    @if (nzTitle || nzDescription) {
      <div class="ant-card-meta-detail">
        @if (nzTitle) {
          <div class="ant-card-meta-title">
            <ng-container *nzStringTemplateOutlet="nzTitle">{{ nzTitle }}</ng-container>
          </div>
        }
        @if (nzDescription) {
          <div class="ant-card-meta-description">
            <ng-container *nzStringTemplateOutlet="nzDescription">{{ nzDescription }}</ng-container>
          </div>
        }
      </div>
    }
  `,
      host: {
        class: "ant-card-meta"
      },
      imports: [NgTemplateOutlet, NzOutletModule]
    }]
  }], null, {
    nzTitle: [{
      type: Input
    }],
    nzDescription: [{
      type: Input
    }],
    nzAvatar: [{
      type: Input
    }]
  });
})();
var NzCardTabComponent = class _NzCardTabComponent {
  template;
  static \u0275fac = function NzCardTabComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzCardTabComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzCardTabComponent,
    selectors: [["nz-card-tab"]],
    viewQuery: function NzCardTabComponent_Query(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275viewQuery(TemplateRef, 7);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.template = _t.first);
      }
    },
    exportAs: ["nzCardTab"],
    ngContentSelectors: _c02,
    decls: 1,
    vars: 0,
    template: function NzCardTabComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275template(0, NzCardTabComponent_ng_template_0_Template, 1, 0, "ng-template");
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzCardTabComponent, [{
    type: Component,
    args: [{
      selector: "nz-card-tab",
      exportAs: "nzCardTab",
      encapsulation: ViewEncapsulation.None,
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `
    <ng-template>
      <ng-content></ng-content>
    </ng-template>
  `
    }]
  }], null, {
    template: [{
      type: ViewChild,
      args: [TemplateRef, {
        static: true
      }]
    }]
  });
})();
var NZ_CONFIG_MODULE_NAME = "card";
var NzCardComponent = (() => {
  let _nzBordered_decorators;
  let _nzBordered_initializers = [];
  let _nzBordered_extraInitializers = [];
  let _nzHoverable_decorators;
  let _nzHoverable_initializers = [];
  let _nzHoverable_extraInitializers = [];
  let _nzSize_decorators;
  let _nzSize_initializers = [];
  let _nzSize_extraInitializers = [];
  return class NzCardComponent2 {
    static {
      const _metadata = typeof Symbol === "function" && Symbol.metadata ? /* @__PURE__ */ Object.create(null) : void 0;
      _nzBordered_decorators = [WithConfig()];
      _nzHoverable_decorators = [WithConfig()];
      _nzSize_decorators = [WithConfig()];
      __esDecorate(null, null, _nzBordered_decorators, {
        kind: "field",
        name: "nzBordered",
        static: false,
        private: false,
        access: {
          has: (obj) => "nzBordered" in obj,
          get: (obj) => obj.nzBordered,
          set: (obj, value) => {
            obj.nzBordered = value;
          }
        },
        metadata: _metadata
      }, _nzBordered_initializers, _nzBordered_extraInitializers);
      __esDecorate(null, null, _nzHoverable_decorators, {
        kind: "field",
        name: "nzHoverable",
        static: false,
        private: false,
        access: {
          has: (obj) => "nzHoverable" in obj,
          get: (obj) => obj.nzHoverable,
          set: (obj, value) => {
            obj.nzHoverable = value;
          }
        },
        metadata: _metadata
      }, _nzHoverable_initializers, _nzHoverable_extraInitializers);
      __esDecorate(null, null, _nzSize_decorators, {
        kind: "field",
        name: "nzSize",
        static: false,
        private: false,
        access: {
          has: (obj) => "nzSize" in obj,
          get: (obj) => obj.nzSize,
          set: (obj, value) => {
            obj.nzSize = value;
          }
        },
        metadata: _metadata
      }, _nzSize_initializers, _nzSize_extraInitializers);
      if (_metadata) Object.defineProperty(this, Symbol.metadata, {
        enumerable: true,
        configurable: true,
        writable: true,
        value: _metadata
      });
    }
    nzConfigService;
    cdr;
    directionality;
    _nzModuleName = NZ_CONFIG_MODULE_NAME;
    nzBordered = __runInitializers(this, _nzBordered_initializers, true);
    nzLoading = (__runInitializers(this, _nzBordered_extraInitializers), false);
    nzHoverable = __runInitializers(this, _nzHoverable_initializers, false);
    nzBodyStyle = (__runInitializers(this, _nzHoverable_extraInitializers), null);
    nzCover;
    nzActions = [];
    nzType = null;
    nzSize = __runInitializers(this, _nzSize_initializers, "default");
    nzTitle = __runInitializers(this, _nzSize_extraInitializers);
    nzExtra;
    listOfNzCardTabComponent;
    listOfNzCardGridDirective;
    dir = "ltr";
    destroy$ = new Subject();
    constructor(nzConfigService, cdr, directionality) {
      this.nzConfigService = nzConfigService;
      this.cdr = cdr;
      this.directionality = directionality;
      this.nzConfigService.getConfigChangeEventForComponent(NZ_CONFIG_MODULE_NAME).pipe(takeUntil(this.destroy$)).subscribe(() => {
        this.cdr.markForCheck();
      });
    }
    ngOnInit() {
      this.directionality.change?.pipe(takeUntil(this.destroy$)).subscribe((direction) => {
        this.dir = direction;
        this.cdr.detectChanges();
      });
      this.dir = this.directionality.value;
    }
    ngOnDestroy() {
      this.destroy$.next(true);
      this.destroy$.complete();
    }
    static \u0275fac = function NzCardComponent_Factory(__ngFactoryType__) {
      return new (__ngFactoryType__ || NzCardComponent2)(\u0275\u0275directiveInject(NzConfigService), \u0275\u0275directiveInject(ChangeDetectorRef), \u0275\u0275directiveInject(Directionality));
    };
    static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
      type: NzCardComponent2,
      selectors: [["nz-card"]],
      contentQueries: function NzCardComponent_ContentQueries(rf, ctx, dirIndex) {
        if (rf & 1) {
          \u0275\u0275contentQuery(dirIndex, NzCardTabComponent, 5);
          \u0275\u0275contentQuery(dirIndex, NzCardGridDirective, 4);
        }
        if (rf & 2) {
          let _t;
          \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.listOfNzCardTabComponent = _t.first);
          \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.listOfNzCardGridDirective = _t);
        }
      },
      hostAttrs: [1, "ant-card"],
      hostVars: 16,
      hostBindings: function NzCardComponent_HostBindings(rf, ctx) {
        if (rf & 2) {
          \u0275\u0275classProp("ant-card-loading", ctx.nzLoading)("ant-card-bordered", ctx.nzBordered)("ant-card-hoverable", ctx.nzHoverable)("ant-card-small", ctx.nzSize === "small")("ant-card-contain-grid", ctx.listOfNzCardGridDirective && ctx.listOfNzCardGridDirective.length)("ant-card-type-inner", ctx.nzType === "inner")("ant-card-contain-tabs", !!ctx.listOfNzCardTabComponent)("ant-card-rtl", ctx.dir === "rtl");
        }
      },
      inputs: {
        nzBordered: [2, "nzBordered", "nzBordered", booleanAttribute],
        nzLoading: [2, "nzLoading", "nzLoading", booleanAttribute],
        nzHoverable: [2, "nzHoverable", "nzHoverable", booleanAttribute],
        nzBodyStyle: "nzBodyStyle",
        nzCover: "nzCover",
        nzActions: "nzActions",
        nzType: "nzType",
        nzSize: "nzSize",
        nzTitle: "nzTitle",
        nzExtra: "nzExtra"
      },
      exportAs: ["nzCard"],
      ngContentSelectors: _c02,
      decls: 6,
      vars: 6,
      consts: [[1, "ant-card-head"], [1, "ant-card-cover"], [1, "ant-card-body"], [3, "nzActive", "nzTitle", "nzParagraph"], [1, "ant-card-actions"], [1, "ant-card-head-wrapper"], [1, "ant-card-head-title"], [1, "ant-card-extra"], [3, "ngTemplateOutlet"], [4, "nzStringTemplateOutlet"], [3, "width"]],
      template: function NzCardComponent_Template(rf, ctx) {
        if (rf & 1) {
          \u0275\u0275projectionDef();
          \u0275\u0275template(0, NzCardComponent_Conditional_0_Template, 5, 3, "div", 0)(1, NzCardComponent_Conditional_1_Template, 2, 1, "div", 1);
          \u0275\u0275elementStart(2, "div", 2);
          \u0275\u0275template(3, NzCardComponent_Conditional_3_Template, 1, 4, "nz-skeleton", 3)(4, NzCardComponent_Conditional_4_Template, 1, 0);
          \u0275\u0275elementEnd();
          \u0275\u0275template(5, NzCardComponent_Conditional_5_Template, 3, 0, "ul", 4);
        }
        if (rf & 2) {
          \u0275\u0275conditional(ctx.nzTitle || ctx.nzExtra || ctx.listOfNzCardTabComponent ? 0 : -1);
          \u0275\u0275advance();
          \u0275\u0275conditional(ctx.nzCover ? 1 : -1);
          \u0275\u0275advance();
          \u0275\u0275styleMap(ctx.nzBodyStyle);
          \u0275\u0275advance();
          \u0275\u0275conditional(ctx.nzLoading ? 3 : 4);
          \u0275\u0275advance(2);
          \u0275\u0275conditional(ctx.nzActions.length ? 5 : -1);
        }
      },
      dependencies: [NzOutletModule, NzStringTemplateOutletDirective, NgTemplateOutlet, NzSkeletonModule, NzSkeletonComponent],
      encapsulation: 2,
      changeDetection: 0
    });
  };
})();
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzCardComponent, [{
    type: Component,
    args: [{
      selector: "nz-card",
      exportAs: "nzCard",
      preserveWhitespaces: false,
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      template: `
    @if (nzTitle || nzExtra || listOfNzCardTabComponent) {
      <div class="ant-card-head">
        <div class="ant-card-head-wrapper">
          @if (nzTitle) {
            <div class="ant-card-head-title">
              <ng-container *nzStringTemplateOutlet="nzTitle">{{ nzTitle }}</ng-container>
            </div>
          }
          @if (nzExtra) {
            <div class="ant-card-extra">
              <ng-container *nzStringTemplateOutlet="nzExtra">{{ nzExtra }}</ng-container>
            </div>
          }
        </div>
        @if (listOfNzCardTabComponent) {
          <ng-template [ngTemplateOutlet]="listOfNzCardTabComponent.template" />
        }
      </div>
    }

    @if (nzCover) {
      <div class="ant-card-cover">
        <ng-template [ngTemplateOutlet]="nzCover" />
      </div>
    }

    <div class="ant-card-body" [style]="nzBodyStyle">
      @if (nzLoading) {
        <nz-skeleton [nzActive]="true" [nzTitle]="false" [nzParagraph]="{ rows: 4 }"></nz-skeleton>
      } @else {
        <ng-content />
      }
    </div>
    @if (nzActions.length) {
      <ul class="ant-card-actions">
        @for (action of nzActions; track $index) {
          <li [style.width.%]="100 / nzActions.length">
            <span><ng-template [ngTemplateOutlet]="action" /></span>
          </li>
        }
      </ul>
    }
  `,
      host: {
        class: "ant-card",
        "[class.ant-card-loading]": "nzLoading",
        "[class.ant-card-bordered]": "nzBordered",
        "[class.ant-card-hoverable]": "nzHoverable",
        "[class.ant-card-small]": 'nzSize === "small"',
        "[class.ant-card-contain-grid]": "listOfNzCardGridDirective && listOfNzCardGridDirective.length",
        "[class.ant-card-type-inner]": 'nzType === "inner"',
        "[class.ant-card-contain-tabs]": "!!listOfNzCardTabComponent",
        "[class.ant-card-rtl]": `dir === 'rtl'`
      },
      imports: [NzOutletModule, NgTemplateOutlet, NzSkeletonModule]
    }]
  }], () => [{
    type: NzConfigService
  }, {
    type: ChangeDetectorRef
  }, {
    type: Directionality
  }], {
    nzBordered: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzLoading: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzHoverable: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzBodyStyle: [{
      type: Input
    }],
    nzCover: [{
      type: Input
    }],
    nzActions: [{
      type: Input
    }],
    nzType: [{
      type: Input
    }],
    nzSize: [{
      type: Input
    }],
    nzTitle: [{
      type: Input
    }],
    nzExtra: [{
      type: Input
    }],
    listOfNzCardTabComponent: [{
      type: ContentChild,
      args: [NzCardTabComponent, {
        static: false
      }]
    }],
    listOfNzCardGridDirective: [{
      type: ContentChildren,
      args: [NzCardGridDirective]
    }]
  });
})();
var NzCardModule = class _NzCardModule {
  static \u0275fac = function NzCardModule_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzCardModule)();
  };
  static \u0275mod = /* @__PURE__ */ \u0275\u0275defineNgModule({
    type: _NzCardModule
  });
  static \u0275inj = /* @__PURE__ */ \u0275\u0275defineInjector({
    imports: [NzCardComponent, NzCardMetaComponent, BidiModule]
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzCardModule, [{
    type: NgModule,
    args: [{
      imports: [NzCardComponent, NzCardGridDirective, NzCardMetaComponent, NzCardTabComponent],
      exports: [BidiModule, NzCardComponent, NzCardGridDirective, NzCardMetaComponent, NzCardTabComponent]
    }]
  }], null, null);
})();

// node_modules/ng-zorro-antd/fesm2022/ng-zorro-antd-spin.mjs
var _c03 = ["*"];
function NzSpinComponent_ng_template_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span", 2);
    \u0275\u0275element(1, "i", 3)(2, "i", 3)(3, "i", 3)(4, "i", 3);
    \u0275\u0275elementEnd();
  }
}
function NzSpinComponent_Conditional_2_ng_template_2_Template(rf, ctx) {
}
function NzSpinComponent_Conditional_2_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 6);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzTip);
  }
}
function NzSpinComponent_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div")(1, "div", 4);
    \u0275\u0275template(2, NzSpinComponent_Conditional_2_ng_template_2_Template, 0, 0, "ng-template", 5)(3, NzSpinComponent_Conditional_2_Conditional_3_Template, 2, 1, "div", 6);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    const defaultTemplate_r2 = \u0275\u0275reference(1);
    \u0275\u0275advance();
    \u0275\u0275classProp("ant-spin-rtl", ctx_r0.dir === "rtl")("ant-spin-spinning", ctx_r0.isLoading)("ant-spin-lg", ctx_r0.nzSize === "large")("ant-spin-sm", ctx_r0.nzSize === "small")("ant-spin-show-text", ctx_r0.nzTip);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzIndicator || defaultTemplate_r2);
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.nzTip ? 3 : -1);
  }
}
function NzSpinComponent_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 7);
    \u0275\u0275projection(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275classProp("ant-spin-blur", ctx_r0.isLoading);
  }
}
var NZ_CONFIG_MODULE_NAME2 = "spin";
var NzSpinComponent = (() => {
  let _nzIndicator_decorators;
  let _nzIndicator_initializers = [];
  let _nzIndicator_extraInitializers = [];
  return class NzSpinComponent2 {
    static {
      const _metadata = typeof Symbol === "function" && Symbol.metadata ? /* @__PURE__ */ Object.create(null) : void 0;
      _nzIndicator_decorators = [WithConfig()];
      __esDecorate(null, null, _nzIndicator_decorators, {
        kind: "field",
        name: "nzIndicator",
        static: false,
        private: false,
        access: {
          has: (obj) => "nzIndicator" in obj,
          get: (obj) => obj.nzIndicator,
          set: (obj, value) => {
            obj.nzIndicator = value;
          }
        },
        metadata: _metadata
      }, _nzIndicator_initializers, _nzIndicator_extraInitializers);
      if (_metadata) Object.defineProperty(this, Symbol.metadata, {
        enumerable: true,
        configurable: true,
        writable: true,
        value: _metadata
      });
    }
    nzConfigService;
    cdr;
    directionality;
    _nzModuleName = NZ_CONFIG_MODULE_NAME2;
    nzIndicator = __runInitializers(this, _nzIndicator_initializers, null);
    nzSize = (__runInitializers(this, _nzIndicator_extraInitializers), "default");
    nzTip = null;
    nzDelay = 0;
    nzSimple = false;
    nzSpinning = true;
    destroy$ = new Subject();
    spinning$ = new BehaviorSubject(this.nzSpinning);
    delay$ = new ReplaySubject(1);
    isLoading = false;
    dir = "ltr";
    constructor(nzConfigService, cdr, directionality) {
      this.nzConfigService = nzConfigService;
      this.cdr = cdr;
      this.directionality = directionality;
    }
    ngOnInit() {
      const loading$ = this.delay$.pipe(startWith(this.nzDelay), distinctUntilChanged(), switchMap((delay) => {
        if (delay === 0) {
          return this.spinning$;
        }
        return this.spinning$.pipe(debounce((spinning) => timer(spinning ? delay : 0)));
      }), takeUntil(this.destroy$));
      loading$.subscribe((loading) => {
        this.isLoading = loading;
        this.cdr.markForCheck();
      });
      this.nzConfigService.getConfigChangeEventForComponent(NZ_CONFIG_MODULE_NAME2).pipe(takeUntil(this.destroy$)).subscribe(() => this.cdr.markForCheck());
      this.directionality.change?.pipe(takeUntil(this.destroy$)).subscribe((direction) => {
        this.dir = direction;
        this.cdr.detectChanges();
      });
      this.dir = this.directionality.value;
    }
    ngOnChanges(changes) {
      const {
        nzSpinning,
        nzDelay
      } = changes;
      if (nzSpinning) {
        this.spinning$.next(this.nzSpinning);
      }
      if (nzDelay) {
        this.delay$.next(this.nzDelay);
      }
    }
    ngOnDestroy() {
      this.destroy$.next();
      this.destroy$.complete();
    }
    static \u0275fac = function NzSpinComponent_Factory(__ngFactoryType__) {
      return new (__ngFactoryType__ || NzSpinComponent2)(\u0275\u0275directiveInject(NzConfigService), \u0275\u0275directiveInject(ChangeDetectorRef), \u0275\u0275directiveInject(Directionality));
    };
    static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
      type: NzSpinComponent2,
      selectors: [["nz-spin"]],
      hostVars: 2,
      hostBindings: function NzSpinComponent_HostBindings(rf, ctx) {
        if (rf & 2) {
          \u0275\u0275classProp("ant-spin-nested-loading", !ctx.nzSimple);
        }
      },
      inputs: {
        nzIndicator: "nzIndicator",
        nzSize: "nzSize",
        nzTip: "nzTip",
        nzDelay: [2, "nzDelay", "nzDelay", numberAttribute],
        nzSimple: [2, "nzSimple", "nzSimple", booleanAttribute],
        nzSpinning: [2, "nzSpinning", "nzSpinning", booleanAttribute]
      },
      exportAs: ["nzSpin"],
      features: [\u0275\u0275NgOnChangesFeature],
      ngContentSelectors: _c03,
      decls: 4,
      vars: 2,
      consts: [["defaultTemplate", ""], [1, "ant-spin-container", 3, "ant-spin-blur"], [1, "ant-spin-dot", "ant-spin-dot-spin"], [1, "ant-spin-dot-item"], [1, "ant-spin"], [3, "ngTemplateOutlet"], [1, "ant-spin-text"], [1, "ant-spin-container"]],
      template: function NzSpinComponent_Template(rf, ctx) {
        if (rf & 1) {
          \u0275\u0275projectionDef();
          \u0275\u0275template(0, NzSpinComponent_ng_template_0_Template, 5, 0, "ng-template", null, 0, \u0275\u0275templateRefExtractor)(2, NzSpinComponent_Conditional_2_Template, 4, 12, "div")(3, NzSpinComponent_Conditional_3_Template, 2, 2, "div", 1);
        }
        if (rf & 2) {
          \u0275\u0275advance(2);
          \u0275\u0275conditional(ctx.isLoading ? 2 : -1);
          \u0275\u0275advance();
          \u0275\u0275conditional(!ctx.nzSimple ? 3 : -1);
        }
      },
      dependencies: [NgTemplateOutlet],
      encapsulation: 2
    });
  };
})();
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSpinComponent, [{
    type: Component,
    args: [{
      selector: "nz-spin",
      exportAs: "nzSpin",
      preserveWhitespaces: false,
      encapsulation: ViewEncapsulation.None,
      template: `
    <ng-template #defaultTemplate>
      <span class="ant-spin-dot ant-spin-dot-spin">
        <i class="ant-spin-dot-item"></i>
        <i class="ant-spin-dot-item"></i>
        <i class="ant-spin-dot-item"></i>
        <i class="ant-spin-dot-item"></i>
      </span>
    </ng-template>
    @if (isLoading) {
      <div>
        <div
          class="ant-spin"
          [class.ant-spin-rtl]="dir === 'rtl'"
          [class.ant-spin-spinning]="isLoading"
          [class.ant-spin-lg]="nzSize === 'large'"
          [class.ant-spin-sm]="nzSize === 'small'"
          [class.ant-spin-show-text]="nzTip"
        >
          <ng-template [ngTemplateOutlet]="nzIndicator || defaultTemplate"></ng-template>
          @if (nzTip) {
            <div class="ant-spin-text">{{ nzTip }}</div>
          }
        </div>
      </div>
    }
    @if (!nzSimple) {
      <div class="ant-spin-container" [class.ant-spin-blur]="isLoading">
        <ng-content></ng-content>
      </div>
    }
  `,
      host: {
        "[class.ant-spin-nested-loading]": "!nzSimple"
      },
      imports: [NgTemplateOutlet]
    }]
  }], () => [{
    type: NzConfigService
  }, {
    type: ChangeDetectorRef
  }, {
    type: Directionality
  }], {
    nzIndicator: [{
      type: Input
    }],
    nzSize: [{
      type: Input
    }],
    nzTip: [{
      type: Input
    }],
    nzDelay: [{
      type: Input,
      args: [{
        transform: numberAttribute
      }]
    }],
    nzSimple: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzSpinning: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }]
  });
})();
var NzSpinModule = class _NzSpinModule {
  static \u0275fac = function NzSpinModule_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzSpinModule)();
  };
  static \u0275mod = /* @__PURE__ */ \u0275\u0275defineNgModule({
    type: _NzSpinModule
  });
  static \u0275inj = /* @__PURE__ */ \u0275\u0275defineInjector({});
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzSpinModule, [{
    type: NgModule,
    args: [{
      imports: [NzSpinComponent],
      exports: [NzSpinComponent]
    }]
  }], null, null);
})();

// node_modules/ng-zorro-antd/fesm2022/ng-zorro-antd-empty.mjs
function NzEmptyComponent_Conditional_1_ng_container_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275element(1, "img", 4);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("src", ctx_r0.nzNotFoundImage, \u0275\u0275sanitizeUrl)("alt", ctx_r0.isContentString ? ctx_r0.nzNotFoundContent : "empty");
  }
}
function NzEmptyComponent_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzEmptyComponent_Conditional_1_ng_container_0_Template, 2, 2, "ng-container", 3);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzNotFoundImage);
  }
}
function NzEmptyComponent_Conditional_2_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-empty-simple");
  }
}
function NzEmptyComponent_Conditional_2_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-empty-default");
  }
}
function NzEmptyComponent_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzEmptyComponent_Conditional_2_Conditional_0_Template, 1, 0, "nz-empty-simple")(1, NzEmptyComponent_Conditional_2_Conditional_1_Template, 1, 0, "nz-empty-default");
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275conditional(ctx_r0.nzNotFoundImage === "simple" ? 0 : 1);
  }
}
function NzEmptyComponent_Conditional_3_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", ctx_r0.isContentString ? ctx_r0.nzNotFoundContent : ctx_r0.locale["description"], " ");
  }
}
function NzEmptyComponent_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 1);
    \u0275\u0275template(1, NzEmptyComponent_Conditional_3_ng_container_1_Template, 2, 1, "ng-container", 3);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzNotFoundContent);
  }
}
function NzEmptyComponent_Conditional_4_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", ctx_r0.nzNotFoundFooter, " ");
  }
}
function NzEmptyComponent_Conditional_4_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 2);
    \u0275\u0275template(1, NzEmptyComponent_Conditional_4_ng_container_1_Template, 2, 1, "ng-container", 3);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzNotFoundFooter);
  }
}
function NzEmbedEmptyComponent_Conditional_0_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275text(0);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275textInterpolate1(" ", ctx_r0.content, " ");
  }
}
function NzEmbedEmptyComponent_Conditional_0_Conditional_1_ng_template_0_Template(rf, ctx) {
}
function NzEmbedEmptyComponent_Conditional_0_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzEmbedEmptyComponent_Conditional_0_Conditional_1_ng_template_0_Template, 0, 0, "ng-template", 0);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275property("cdkPortalOutlet", ctx_r0.contentPortal);
  }
}
function NzEmbedEmptyComponent_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzEmbedEmptyComponent_Conditional_0_Conditional_0_Template, 1, 1)(1, NzEmbedEmptyComponent_Conditional_0_Conditional_1_Template, 1, 1, null, 0);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275conditional(ctx_r0.contentType === "string" ? 0 : 1);
  }
}
function NzEmbedEmptyComponent_Conditional_1_Conditional_0_Case_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-empty", 1);
  }
}
function NzEmbedEmptyComponent_Conditional_1_Conditional_0_Case_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-empty", 2);
  }
}
function NzEmbedEmptyComponent_Conditional_1_Conditional_0_Case_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-empty");
  }
}
function NzEmbedEmptyComponent_Conditional_1_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzEmbedEmptyComponent_Conditional_1_Conditional_0_Case_0_Template, 1, 0, "nz-empty", 1)(1, NzEmbedEmptyComponent_Conditional_1_Conditional_0_Case_1_Template, 1, 0, "nz-empty", 2)(2, NzEmbedEmptyComponent_Conditional_1_Conditional_0_Case_2_Template, 1, 0, "nz-empty");
  }
  if (rf & 2) {
    let tmp_2_0;
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275conditional((tmp_2_0 = ctx_r0.size) === "normal" ? 0 : tmp_2_0 === "small" ? 1 : 2);
  }
}
function NzEmbedEmptyComponent_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzEmbedEmptyComponent_Conditional_1_Conditional_0_Template, 3, 1);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275conditional(ctx_r0.specificContent !== null ? 0 : -1);
  }
}
var NZ_EMPTY_COMPONENT_NAME = new InjectionToken("nz-empty-component-name");
var NzEmptyDefaultComponent = class _NzEmptyDefaultComponent {
  static \u0275fac = function NzEmptyDefaultComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzEmptyDefaultComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzEmptyDefaultComponent,
    selectors: [["nz-empty-default"]],
    exportAs: ["nzEmptyDefault"],
    decls: 12,
    vars: 0,
    consts: [["width", "184", "height", "152", "viewBox", "0 0 184 152", "xmlns", "http://www.w3.org/2000/svg", 1, "ant-empty-img-default"], ["fill", "none", "fill-rule", "evenodd"], ["transform", "translate(24 31.67)"], ["cx", "67.797", "cy", "106.89", "rx", "67.797", "ry", "12.668", 1, "ant-empty-img-default-ellipse"], ["d", "M122.034 69.674L98.109 40.229c-1.148-1.386-2.826-2.225-4.593-2.225h-51.44c-1.766 0-3.444.839-4.592 2.225L13.56 69.674v15.383h108.475V69.674z", 1, "ant-empty-img-default-path-1"], ["d", "M101.537 86.214L80.63 61.102c-1.001-1.207-2.507-1.867-4.048-1.867H31.724c-1.54 0-3.047.66-4.048 1.867L6.769 86.214v13.792h94.768V86.214z", "transform", "translate(13.56)", 1, "ant-empty-img-default-path-2"], ["d", "M33.83 0h67.933a4 4 0 0 1 4 4v93.344a4 4 0 0 1-4 4H33.83a4 4 0 0 1-4-4V4a4 4 0 0 1 4-4z", 1, "ant-empty-img-default-path-3"], ["d", "M42.678 9.953h50.237a2 2 0 0 1 2 2V36.91a2 2 0 0 1-2 2H42.678a2 2 0 0 1-2-2V11.953a2 2 0 0 1 2-2zM42.94 49.767h49.713a2.262 2.262 0 1 1 0 4.524H42.94a2.262 2.262 0 0 1 0-4.524zM42.94 61.53h49.713a2.262 2.262 0 1 1 0 4.525H42.94a2.262 2.262 0 0 1 0-4.525zM121.813 105.032c-.775 3.071-3.497 5.36-6.735 5.36H20.515c-3.238 0-5.96-2.29-6.734-5.36a7.309 7.309 0 0 1-.222-1.79V69.675h26.318c2.907 0 5.25 2.448 5.25 5.42v.04c0 2.971 2.37 5.37 5.277 5.37h34.785c2.907 0 5.277-2.421 5.277-5.393V75.1c0-2.972 2.343-5.426 5.25-5.426h26.318v33.569c0 .617-.077 1.216-.221 1.789z", 1, "ant-empty-img-default-path-4"], ["d", "M149.121 33.292l-6.83 2.65a1 1 0 0 1-1.317-1.23l1.937-6.207c-2.589-2.944-4.109-6.534-4.109-10.408C138.802 8.102 148.92 0 161.402 0 173.881 0 184 8.102 184 18.097c0 9.995-10.118 18.097-22.599 18.097-4.528 0-8.744-1.066-12.28-2.902z", 1, "ant-empty-img-default-path-5"], ["transform", "translate(149.65 15.383)", 1, "ant-empty-img-default-g"], ["cx", "20.654", "cy", "3.167", "rx", "2.849", "ry", "2.815"], ["d", "M5.698 5.63H0L2.898.704zM9.259.704h4.985V5.63H9.259z"]],
    template: function NzEmptyDefaultComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275namespaceSVG();
        \u0275\u0275elementStart(0, "svg", 0)(1, "g", 1)(2, "g", 2);
        \u0275\u0275element(3, "ellipse", 3)(4, "path", 4)(5, "path", 5)(6, "path", 6)(7, "path", 7);
        \u0275\u0275elementEnd();
        \u0275\u0275element(8, "path", 8);
        \u0275\u0275elementStart(9, "g", 9);
        \u0275\u0275element(10, "ellipse", 10)(11, "path", 11);
        \u0275\u0275elementEnd()()();
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzEmptyDefaultComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      selector: "nz-empty-default",
      exportAs: "nzEmptyDefault",
      template: `
    <svg
      class="ant-empty-img-default"
      width="184"
      height="152"
      viewBox="0 0 184 152"
      xmlns="http://www.w3.org/2000/svg"
    >
      <g fill="none" fill-rule="evenodd">
        <g transform="translate(24 31.67)">
          <ellipse class="ant-empty-img-default-ellipse" cx="67.797" cy="106.89" rx="67.797" ry="12.668" />
          <path
            class="ant-empty-img-default-path-1"
            d="M122.034 69.674L98.109 40.229c-1.148-1.386-2.826-2.225-4.593-2.225h-51.44c-1.766 0-3.444.839-4.592 2.225L13.56 69.674v15.383h108.475V69.674z"
          />
          <path
            class="ant-empty-img-default-path-2"
            d="M101.537 86.214L80.63 61.102c-1.001-1.207-2.507-1.867-4.048-1.867H31.724c-1.54 0-3.047.66-4.048 1.867L6.769 86.214v13.792h94.768V86.214z"
            transform="translate(13.56)"
          />
          <path
            class="ant-empty-img-default-path-3"
            d="M33.83 0h67.933a4 4 0 0 1 4 4v93.344a4 4 0 0 1-4 4H33.83a4 4 0 0 1-4-4V4a4 4 0 0 1 4-4z"
          />
          <path
            class="ant-empty-img-default-path-4"
            d="M42.678 9.953h50.237a2 2 0 0 1 2 2V36.91a2 2 0 0 1-2 2H42.678a2 2 0 0 1-2-2V11.953a2 2 0 0 1 2-2zM42.94 49.767h49.713a2.262 2.262 0 1 1 0 4.524H42.94a2.262 2.262 0 0 1 0-4.524zM42.94 61.53h49.713a2.262 2.262 0 1 1 0 4.525H42.94a2.262 2.262 0 0 1 0-4.525zM121.813 105.032c-.775 3.071-3.497 5.36-6.735 5.36H20.515c-3.238 0-5.96-2.29-6.734-5.36a7.309 7.309 0 0 1-.222-1.79V69.675h26.318c2.907 0 5.25 2.448 5.25 5.42v.04c0 2.971 2.37 5.37 5.277 5.37h34.785c2.907 0 5.277-2.421 5.277-5.393V75.1c0-2.972 2.343-5.426 5.25-5.426h26.318v33.569c0 .617-.077 1.216-.221 1.789z"
          />
        </g>
        <path
          class="ant-empty-img-default-path-5"
          d="M149.121 33.292l-6.83 2.65a1 1 0 0 1-1.317-1.23l1.937-6.207c-2.589-2.944-4.109-6.534-4.109-10.408C138.802 8.102 148.92 0 161.402 0 173.881 0 184 8.102 184 18.097c0 9.995-10.118 18.097-22.599 18.097-4.528 0-8.744-1.066-12.28-2.902z"
        />
        <g class="ant-empty-img-default-g" transform="translate(149.65 15.383)">
          <ellipse cx="20.654" cy="3.167" rx="2.849" ry="2.815" />
          <path d="M5.698 5.63H0L2.898.704zM9.259.704h4.985V5.63H9.259z" />
        </g>
      </g>
    </svg>
  `
    }]
  }], null, null);
})();
var NzEmptySimpleComponent = class _NzEmptySimpleComponent {
  static \u0275fac = function NzEmptySimpleComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzEmptySimpleComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzEmptySimpleComponent,
    selectors: [["nz-empty-simple"]],
    exportAs: ["nzEmptySimple"],
    decls: 6,
    vars: 0,
    consts: [["width", "64", "height", "41", "viewBox", "0 0 64 41", "xmlns", "http://www.w3.org/2000/svg", 1, "ant-empty-img-simple"], ["transform", "translate(0 1)", "fill", "none", "fill-rule", "evenodd"], ["cx", "32", "cy", "33", "rx", "32", "ry", "7", 1, "ant-empty-img-simple-ellipse"], ["fill-rule", "nonzero", 1, "ant-empty-img-simple-g"], ["d", "M55 12.76L44.854 1.258C44.367.474 43.656 0 42.907 0H21.093c-.749 0-1.46.474-1.947 1.257L9 12.761V22h46v-9.24z"], ["d", "M41.613 15.931c0-1.605.994-2.93 2.227-2.931H55v18.137C55 33.26 53.68 35 52.05 35h-40.1C10.32 35 9 33.259 9 31.137V13h11.16c1.233 0 2.227 1.323 2.227 2.928v.022c0 1.605 1.005 2.901 2.237 2.901h14.752c1.232 0 2.237-1.308 2.237-2.913v-.007z", 1, "ant-empty-img-simple-path"]],
    template: function NzEmptySimpleComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275namespaceSVG();
        \u0275\u0275elementStart(0, "svg", 0)(1, "g", 1);
        \u0275\u0275element(2, "ellipse", 2);
        \u0275\u0275elementStart(3, "g", 3);
        \u0275\u0275element(4, "path", 4)(5, "path", 5);
        \u0275\u0275elementEnd()()();
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzEmptySimpleComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      selector: "nz-empty-simple",
      exportAs: "nzEmptySimple",
      template: `
    <svg class="ant-empty-img-simple" width="64" height="41" viewBox="0 0 64 41" xmlns="http://www.w3.org/2000/svg">
      <g transform="translate(0 1)" fill="none" fill-rule="evenodd">
        <ellipse class="ant-empty-img-simple-ellipse" cx="32" cy="33" rx="32" ry="7" />
        <g class="ant-empty-img-simple-g" fill-rule="nonzero">
          <path
            d="M55 12.76L44.854 1.258C44.367.474 43.656 0 42.907 0H21.093c-.749 0-1.46.474-1.947 1.257L9 12.761V22h46v-9.24z"
          />
          <path
            d="M41.613 15.931c0-1.605.994-2.93 2.227-2.931H55v18.137C55 33.26 53.68 35 52.05 35h-40.1C10.32 35 9 33.259 9 31.137V13h11.16c1.233 0 2.227 1.323 2.227 2.928v.022c0 1.605 1.005 2.901 2.237 2.901h14.752c1.232 0 2.237-1.308 2.237-2.913v-.007z"
            class="ant-empty-img-simple-path"
          />
        </g>
      </g>
    </svg>
  `
    }]
  }], null, null);
})();
var NzEmptyDefaultImages = ["default", "simple"];
var NzEmptyComponent = class _NzEmptyComponent {
  i18n;
  cdr;
  nzNotFoundImage = "default";
  nzNotFoundContent;
  nzNotFoundFooter;
  isContentString = false;
  isImageBuildIn = true;
  locale;
  destroy$ = new Subject();
  constructor(i18n, cdr) {
    this.i18n = i18n;
    this.cdr = cdr;
  }
  ngOnChanges(changes) {
    const {
      nzNotFoundContent,
      nzNotFoundImage
    } = changes;
    if (nzNotFoundContent) {
      const content = nzNotFoundContent.currentValue;
      this.isContentString = typeof content === "string";
    }
    if (nzNotFoundImage) {
      const image = nzNotFoundImage.currentValue || "default";
      this.isImageBuildIn = NzEmptyDefaultImages.findIndex((i) => i === image) > -1;
    }
  }
  ngOnInit() {
    this.i18n.localeChange.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.locale = this.i18n.getLocaleData("Empty");
      this.cdr.markForCheck();
    });
  }
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
  static \u0275fac = function NzEmptyComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzEmptyComponent)(\u0275\u0275directiveInject(NzI18nService), \u0275\u0275directiveInject(ChangeDetectorRef));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzEmptyComponent,
    selectors: [["nz-empty"]],
    hostAttrs: [1, "ant-empty"],
    inputs: {
      nzNotFoundImage: "nzNotFoundImage",
      nzNotFoundContent: "nzNotFoundContent",
      nzNotFoundFooter: "nzNotFoundFooter"
    },
    exportAs: ["nzEmpty"],
    features: [\u0275\u0275NgOnChangesFeature],
    decls: 5,
    vars: 3,
    consts: [[1, "ant-empty-image"], [1, "ant-empty-description"], [1, "ant-empty-footer"], [4, "nzStringTemplateOutlet"], [3, "src", "alt"]],
    template: function NzEmptyComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275elementStart(0, "div", 0);
        \u0275\u0275template(1, NzEmptyComponent_Conditional_1_Template, 1, 1, "ng-container")(2, NzEmptyComponent_Conditional_2_Template, 2, 1);
        \u0275\u0275elementEnd();
        \u0275\u0275template(3, NzEmptyComponent_Conditional_3_Template, 2, 1, "p", 1)(4, NzEmptyComponent_Conditional_4_Template, 2, 1, "div", 2);
      }
      if (rf & 2) {
        \u0275\u0275advance();
        \u0275\u0275conditional(!ctx.isImageBuildIn ? 1 : 2);
        \u0275\u0275advance(2);
        \u0275\u0275conditional(ctx.nzNotFoundContent !== null ? 3 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(ctx.nzNotFoundFooter ? 4 : -1);
      }
    },
    dependencies: [NzOutletModule, NzStringTemplateOutletDirective, NzEmptyDefaultComponent, NzEmptySimpleComponent],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzEmptyComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      selector: "nz-empty",
      exportAs: "nzEmpty",
      template: `
    <div class="ant-empty-image">
      @if (!isImageBuildIn) {
        <ng-container *nzStringTemplateOutlet="nzNotFoundImage">
          <img [src]="nzNotFoundImage" [alt]="isContentString ? nzNotFoundContent : 'empty'" />
        </ng-container>
      } @else {
        @if (nzNotFoundImage === 'simple') {
          <nz-empty-simple />
        } @else {
          <nz-empty-default />
        }
      }
    </div>
    @if (nzNotFoundContent !== null) {
      <p class="ant-empty-description">
        <ng-container *nzStringTemplateOutlet="nzNotFoundContent">
          {{ isContentString ? nzNotFoundContent : locale['description'] }}
        </ng-container>
      </p>
    }

    @if (nzNotFoundFooter) {
      <div class="ant-empty-footer">
        <ng-container *nzStringTemplateOutlet="nzNotFoundFooter">
          {{ nzNotFoundFooter }}
        </ng-container>
      </div>
    }
  `,
      host: {
        class: "ant-empty"
      },
      imports: [NzOutletModule, NzEmptyDefaultComponent, NzEmptySimpleComponent]
    }]
  }], () => [{
    type: NzI18nService
  }, {
    type: ChangeDetectorRef
  }], {
    nzNotFoundImage: [{
      type: Input
    }],
    nzNotFoundContent: [{
      type: Input
    }],
    nzNotFoundFooter: [{
      type: Input
    }]
  });
})();
function getEmptySize(componentName) {
  switch (componentName) {
    case "table":
    case "list":
      return "normal";
    case "select":
    case "tree-select":
    case "cascader":
    case "transfer":
      return "small";
    default:
      return "";
  }
}
var NzEmbedEmptyComponent = class _NzEmbedEmptyComponent {
  configService;
  viewContainerRef;
  cdr;
  injector;
  nzComponentName;
  specificContent;
  content;
  contentType = "string";
  contentPortal;
  size = "";
  destroy$ = new Subject();
  constructor(configService, viewContainerRef, cdr, injector) {
    this.configService = configService;
    this.viewContainerRef = viewContainerRef;
    this.cdr = cdr;
    this.injector = injector;
  }
  ngOnChanges(changes) {
    if (changes.nzComponentName) {
      this.size = getEmptySize(changes.nzComponentName.currentValue);
    }
    if (changes.specificContent && !changes.specificContent.isFirstChange()) {
      this.content = changes.specificContent.currentValue;
      this.renderEmpty();
    }
  }
  ngOnInit() {
    this.subscribeDefaultEmptyContentChange();
  }
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
  renderEmpty() {
    const content = this.content;
    if (typeof content === "string") {
      this.contentType = "string";
    } else if (content instanceof TemplateRef) {
      const context = {
        $implicit: this.nzComponentName
      };
      this.contentType = "template";
      this.contentPortal = new TemplatePortal(content, this.viewContainerRef, context);
    } else if (content instanceof Type) {
      const injector = Injector.create({
        parent: this.injector,
        providers: [{
          provide: NZ_EMPTY_COMPONENT_NAME,
          useValue: this.nzComponentName
        }]
      });
      this.contentType = "component";
      this.contentPortal = new ComponentPortal(content, this.viewContainerRef, injector);
    } else {
      this.contentType = "string";
      this.contentPortal = void 0;
    }
    this.cdr.detectChanges();
  }
  subscribeDefaultEmptyContentChange() {
    this.configService.getConfigChangeEventForComponent("empty").pipe(startWith(true), takeUntil(this.destroy$)).subscribe(() => {
      this.content = this.specificContent || this.getUserDefaultEmptyContent();
      this.renderEmpty();
    });
  }
  getUserDefaultEmptyContent() {
    return (this.configService.getConfigForComponent("empty") || {}).nzDefaultEmptyContent;
  }
  static \u0275fac = function NzEmbedEmptyComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzEmbedEmptyComponent)(\u0275\u0275directiveInject(NzConfigService), \u0275\u0275directiveInject(ViewContainerRef), \u0275\u0275directiveInject(ChangeDetectorRef), \u0275\u0275directiveInject(Injector));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzEmbedEmptyComponent,
    selectors: [["nz-embed-empty"]],
    inputs: {
      nzComponentName: "nzComponentName",
      specificContent: "specificContent"
    },
    exportAs: ["nzEmbedEmpty"],
    features: [\u0275\u0275NgOnChangesFeature],
    decls: 2,
    vars: 1,
    consts: [[3, "cdkPortalOutlet"], ["nzNotFoundImage", "simple", 1, "ant-empty-normal"], ["nzNotFoundImage", "simple", 1, "ant-empty-small"]],
    template: function NzEmbedEmptyComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275template(0, NzEmbedEmptyComponent_Conditional_0_Template, 2, 1)(1, NzEmbedEmptyComponent_Conditional_1_Template, 1, 1);
      }
      if (rf & 2) {
        \u0275\u0275conditional(ctx.content ? 0 : 1);
      }
    },
    dependencies: [NzEmptyComponent, PortalModule, CdkPortalOutlet],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzEmbedEmptyComponent, [{
    type: Component,
    args: [{
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      selector: "nz-embed-empty",
      exportAs: "nzEmbedEmpty",
      template: `
    @if (content) {
      @if (contentType === 'string') {
        {{ content }}
      } @else {
        <ng-template [cdkPortalOutlet]="contentPortal" />
      }
    } @else {
      @if (specificContent !== null) {
        @switch (size) {
          @case ('normal') {
            <nz-empty class="ant-empty-normal" nzNotFoundImage="simple" />
          }
          @case ('small') {
            <nz-empty class="ant-empty-small" nzNotFoundImage="simple" />
          }
          @default {
            <nz-empty />
          }
        }
      }
    }
  `,
      imports: [NzEmptyComponent, PortalModule]
    }]
  }], () => [{
    type: NzConfigService
  }, {
    type: ViewContainerRef
  }, {
    type: ChangeDetectorRef
  }, {
    type: Injector
  }], {
    nzComponentName: [{
      type: Input
    }],
    specificContent: [{
      type: Input
    }]
  });
})();
var NzEmptyModule = class _NzEmptyModule {
  static \u0275fac = function NzEmptyModule_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzEmptyModule)();
  };
  static \u0275mod = /* @__PURE__ */ \u0275\u0275defineNgModule({
    type: _NzEmptyModule
  });
  static \u0275inj = /* @__PURE__ */ \u0275\u0275defineInjector({
    imports: [NzEmptyComponent, NzEmbedEmptyComponent]
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzEmptyModule, [{
    type: NgModule,
    args: [{
      imports: [NzEmptyComponent, NzEmbedEmptyComponent, NzEmptyDefaultComponent, NzEmptySimpleComponent],
      exports: [NzEmptyComponent, NzEmbedEmptyComponent]
    }]
  }], null, null);
})();

// node_modules/ng-zorro-antd/fesm2022/ng-zorro-antd-list.mjs
var _c04 = ["*"];
function NzListItemMetaAvatarComponent_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-avatar", 1);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275property("nzSrc", ctx_r0.nzSrc);
  }
}
function NzListItemMetaAvatarComponent_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275projection(0);
  }
}
var _c13 = [[["nz-list-item-meta-avatar"]], [["nz-list-item-meta-title"]], [["nz-list-item-meta-description"]]];
var _c22 = ["nz-list-item-meta-avatar", "nz-list-item-meta-title", "nz-list-item-meta-description"];
function NzListItemMetaComponent_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-list-item-meta-avatar", 0);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275property("nzSrc", ctx_r0.avatarStr);
  }
}
function NzListItemMetaComponent_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-item-meta-avatar");
    \u0275\u0275elementContainer(1, 2);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.avatarTpl);
  }
}
function NzListItemMetaComponent_Conditional_3_Conditional_1_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzTitle);
  }
}
function NzListItemMetaComponent_Conditional_3_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-item-meta-title");
    \u0275\u0275template(1, NzListItemMetaComponent_Conditional_3_Conditional_1_ng_container_1_Template, 2, 1, "ng-container", 3);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzTitle);
  }
}
function NzListItemMetaComponent_Conditional_3_Conditional_2_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzDescription);
  }
}
function NzListItemMetaComponent_Conditional_3_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-item-meta-description");
    \u0275\u0275template(1, NzListItemMetaComponent_Conditional_3_Conditional_2_ng_container_1_Template, 2, 1, "ng-container", 3);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzDescription);
  }
}
function NzListItemMetaComponent_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 1);
    \u0275\u0275template(1, NzListItemMetaComponent_Conditional_3_Conditional_1_Template, 2, 1, "nz-list-item-meta-title")(2, NzListItemMetaComponent_Conditional_3_Conditional_2_Template, 2, 1, "nz-list-item-meta-description");
    \u0275\u0275projection(3, 1);
    \u0275\u0275projection(4, 2);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.nzTitle && !ctx_r0.titleComponent ? 1 : -1);
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.nzDescription && !ctx_r0.descriptionComponent ? 2 : -1);
  }
}
function NzListItemActionComponent_ng_template_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275projection(0);
  }
}
var _c32 = ["nz-list-item-actions", ""];
function NzListItemActionsComponent_For_1_ng_template_1_Template(rf, ctx) {
}
function NzListItemActionsComponent_For_1_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "em", 1);
  }
}
function NzListItemActionsComponent_For_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "li");
    \u0275\u0275template(1, NzListItemActionsComponent_For_1_ng_template_1_Template, 0, 0, "ng-template", 0)(2, NzListItemActionsComponent_For_1_Conditional_2_Template, 1, 0, "em", 1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const i_r1 = ctx.$implicit;
    const \u0275$index_1_r2 = ctx.$index;
    const \u0275$count_1_r3 = ctx.$count;
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", i_r1);
    \u0275\u0275advance();
    \u0275\u0275conditional(!(\u0275$index_1_r2 === \u0275$count_1_r3 - 1) ? 2 : -1);
  }
}
var _c42 = [[["nz-list-header"]], [["nz-list-footer"], ["", "nz-list-footer", ""]], [["nz-list-load-more"], ["", "nz-list-load-more", ""]], [["nz-list-pagination"], ["", "nz-list-pagination", ""]], "*"];
var _c5 = ["nz-list-header", "nz-list-footer, [nz-list-footer]", "nz-list-load-more, [nz-list-load-more]", "nz-list-pagination, [nz-list-pagination]", "*"];
var _c6 = (a0, a1) => ({
  $implicit: a0,
  index: a1
});
function NzListComponent_Conditional_0_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzHeader);
  }
}
function NzListComponent_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-header");
    \u0275\u0275template(1, NzListComponent_Conditional_0_ng_container_1_Template, 2, 1, "ng-container", 6);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzHeader);
  }
}
function NzListComponent_Conditional_4_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "div");
  }
  if (rf & 2) {
    \u0275\u0275styleProp("min-height", 53, "px");
  }
}
function NzListComponent_Conditional_5_For_2_ng_template_1_Template(rf, ctx) {
}
function NzListComponent_Conditional_5_For_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 7);
    \u0275\u0275template(1, NzListComponent_Conditional_5_For_2_ng_template_1_Template, 0, 0, "ng-template", 8);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const item_r2 = ctx.$implicit;
    const \u0275$index_20_r3 = ctx.$index;
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275property("nzSpan", ctx_r0.nzGrid.span || null)("nzXs", ctx_r0.nzGrid.xs || null)("nzSm", ctx_r0.nzGrid.sm || null)("nzMd", ctx_r0.nzGrid.md || null)("nzLg", ctx_r0.nzGrid.lg || null)("nzXl", ctx_r0.nzGrid.xl || null)("nzXXl", ctx_r0.nzGrid.xxl || null);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzRenderItem)("ngTemplateOutletContext", \u0275\u0275pureFunction2(9, _c6, item_r2, \u0275$index_20_r3));
  }
}
function NzListComponent_Conditional_5_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 2);
    \u0275\u0275repeaterCreate(1, NzListComponent_Conditional_5_For_2_Template, 2, 12, "div", 7, \u0275\u0275repeaterTrackByIdentity);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275property("nzGutter", ctx_r0.nzGrid.gutter || null);
    \u0275\u0275advance();
    \u0275\u0275repeater(ctx_r0.nzDataSource);
  }
}
function NzListComponent_Conditional_6_For_2_ng_template_1_Template(rf, ctx) {
}
function NzListComponent_Conditional_6_For_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275template(1, NzListComponent_Conditional_6_For_2_ng_template_1_Template, 0, 0, "ng-template", 8);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const item_r4 = ctx.$implicit;
    const \u0275$index_28_r5 = ctx.$index;
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzRenderItem)("ngTemplateOutletContext", \u0275\u0275pureFunction2(2, _c6, item_r4, \u0275$index_28_r5));
  }
}
function NzListComponent_Conditional_6_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 3);
    \u0275\u0275repeaterCreate(1, NzListComponent_Conditional_6_For_2_Template, 2, 5, "ng-container", null, \u0275\u0275repeaterTrackByIdentity);
    \u0275\u0275projection(3, 4);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275repeater(ctx_r0.nzDataSource);
  }
}
function NzListComponent_Conditional_7_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-list-empty", 4);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275property("nzNoResult", ctx_r0.nzNoResult);
  }
}
function NzListComponent_Conditional_8_ng_container_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzFooter);
  }
}
function NzListComponent_Conditional_8_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-footer");
    \u0275\u0275template(1, NzListComponent_Conditional_8_ng_container_1_Template, 2, 1, "ng-container", 6);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzFooter);
  }
}
function NzListComponent_ng_template_10_Template(rf, ctx) {
}
function NzListComponent_Conditional_12_ng_template_1_Template(rf, ctx) {
}
function NzListComponent_Conditional_12_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-pagination");
    \u0275\u0275template(1, NzListComponent_Conditional_12_ng_template_1_Template, 0, 0, "ng-template", 5);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzPagination);
  }
}
var _c7 = [[["nz-list-item-actions"], ["", "nz-list-item-actions", ""]], [["nz-list-item-meta"], ["", "nz-list-item-meta", ""]], "*", [["nz-list-item-extra"], ["", "nz-list-item-extra", ""]]];
var _c8 = ["nz-list-item-actions, [nz-list-item-actions]", "nz-list-item-meta, [nz-list-item-meta]", "*", "nz-list-item-extra, [nz-list-item-extra]"];
function NzListItemComponent_ng_template_0_Conditional_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "ul", 3);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275property("nzActions", ctx_r0.nzActions);
  }
}
function NzListItemComponent_ng_template_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzListItemComponent_ng_template_0_Conditional_0_Template, 1, 1, "ul", 3);
    \u0275\u0275projection(1);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275conditional(ctx_r0.nzActions && ctx_r0.nzActions.length > 0 ? 0 : -1);
  }
}
function NzListItemComponent_ng_template_2_Conditional_2_ng_container_0_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275text(1);
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(3);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r0.nzContent);
  }
}
function NzListItemComponent_ng_template_2_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzListItemComponent_ng_template_2_Conditional_2_ng_container_0_Template, 2, 1, "ng-container", 4);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275property("nzStringTemplateOutlet", ctx_r0.nzContent);
  }
}
function NzListItemComponent_ng_template_2_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275projection(0, 1);
    \u0275\u0275projection(1, 2);
    \u0275\u0275template(2, NzListItemComponent_ng_template_2_Conditional_2_Template, 1, 1, "ng-container");
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275advance(2);
    \u0275\u0275conditional(ctx_r0.nzContent ? 2 : -1);
  }
}
function NzListItemComponent_ng_template_4_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275projection(0, 3);
  }
}
function NzListItemComponent_Conditional_6_ng_template_1_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_6_ng_template_2_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_6_Conditional_3_ng_template_1_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_6_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-item-extra");
    \u0275\u0275template(1, NzListItemComponent_Conditional_6_Conditional_3_ng_template_1_Template, 0, 0, "ng-template", 6);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext(2);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzExtra);
  }
}
function NzListItemComponent_Conditional_6_ng_template_4_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_6_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 5);
    \u0275\u0275template(1, NzListItemComponent_Conditional_6_ng_template_1_Template, 0, 0, "ng-template", 6)(2, NzListItemComponent_Conditional_6_ng_template_2_Template, 0, 0, "ng-template", 6);
    \u0275\u0275elementEnd();
    \u0275\u0275template(3, NzListItemComponent_Conditional_6_Conditional_3_Template, 2, 1, "nz-list-item-extra")(4, NzListItemComponent_Conditional_6_ng_template_4_Template, 0, 0, "ng-template", 6);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    const actionsTpl_r2 = \u0275\u0275reference(1);
    const contentTpl_r3 = \u0275\u0275reference(3);
    const extraTpl_r4 = \u0275\u0275reference(5);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", contentTpl_r3);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", actionsTpl_r2);
    \u0275\u0275advance();
    \u0275\u0275conditional(ctx_r0.nzExtra ? 3 : -1);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", extraTpl_r4);
  }
}
function NzListItemComponent_Conditional_7_ng_template_0_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_7_ng_template_1_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_7_ng_template_2_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_7_ng_template_3_Template(rf, ctx) {
}
function NzListItemComponent_Conditional_7_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275template(0, NzListItemComponent_Conditional_7_ng_template_0_Template, 0, 0, "ng-template", 6)(1, NzListItemComponent_Conditional_7_ng_template_1_Template, 0, 0, "ng-template", 6)(2, NzListItemComponent_Conditional_7_ng_template_2_Template, 0, 0, "ng-template", 6)(3, NzListItemComponent_Conditional_7_ng_template_3_Template, 0, 0, "ng-template", 6);
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    const actionsTpl_r2 = \u0275\u0275reference(1);
    const contentTpl_r3 = \u0275\u0275reference(3);
    const extraTpl_r4 = \u0275\u0275reference(5);
    \u0275\u0275property("ngTemplateOutlet", contentTpl_r3);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", ctx_r0.nzExtra);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", extraTpl_r4);
    \u0275\u0275advance();
    \u0275\u0275property("ngTemplateOutlet", actionsTpl_r2);
  }
}
var NzListItemMetaTitleComponent = class _NzListItemMetaTitleComponent {
  static \u0275fac = function NzListItemMetaTitleComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemMetaTitleComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemMetaTitleComponent,
    selectors: [["nz-list-item-meta-title"]],
    exportAs: ["nzListItemMetaTitle"],
    ngContentSelectors: _c04,
    decls: 2,
    vars: 0,
    consts: [[1, "ant-list-item-meta-title"]],
    template: function NzListItemMetaTitleComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275elementStart(0, "h4", 0);
        \u0275\u0275projection(1);
        \u0275\u0275elementEnd();
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemMetaTitleComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-item-meta-title",
      exportAs: "nzListItemMetaTitle",
      template: `
    <h4 class="ant-list-item-meta-title">
      <ng-content></ng-content>
    </h4>
  `,
      changeDetection: ChangeDetectionStrategy.OnPush
    }]
  }], null, null);
})();
var NzListItemMetaDescriptionComponent = class _NzListItemMetaDescriptionComponent {
  static \u0275fac = function NzListItemMetaDescriptionComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemMetaDescriptionComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemMetaDescriptionComponent,
    selectors: [["nz-list-item-meta-description"]],
    exportAs: ["nzListItemMetaDescription"],
    ngContentSelectors: _c04,
    decls: 2,
    vars: 0,
    consts: [[1, "ant-list-item-meta-description"]],
    template: function NzListItemMetaDescriptionComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275elementStart(0, "div", 0);
        \u0275\u0275projection(1);
        \u0275\u0275elementEnd();
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemMetaDescriptionComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-item-meta-description",
      exportAs: "nzListItemMetaDescription",
      template: `
    <div class="ant-list-item-meta-description">
      <ng-content></ng-content>
    </div>
  `,
      changeDetection: ChangeDetectionStrategy.OnPush
    }]
  }], null, null);
})();
var NzListItemMetaAvatarComponent = class _NzListItemMetaAvatarComponent {
  nzSrc;
  static \u0275fac = function NzListItemMetaAvatarComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemMetaAvatarComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemMetaAvatarComponent,
    selectors: [["nz-list-item-meta-avatar"]],
    inputs: {
      nzSrc: "nzSrc"
    },
    exportAs: ["nzListItemMetaAvatar"],
    ngContentSelectors: _c04,
    decls: 3,
    vars: 1,
    consts: [[1, "ant-list-item-meta-avatar"], [3, "nzSrc"]],
    template: function NzListItemMetaAvatarComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275elementStart(0, "div", 0);
        \u0275\u0275template(1, NzListItemMetaAvatarComponent_Conditional_1_Template, 1, 1, "nz-avatar", 1)(2, NzListItemMetaAvatarComponent_Conditional_2_Template, 1, 0);
        \u0275\u0275elementEnd();
      }
      if (rf & 2) {
        \u0275\u0275advance();
        \u0275\u0275conditional(ctx.nzSrc ? 1 : 2);
      }
    },
    dependencies: [NzAvatarModule, NzAvatarComponent],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemMetaAvatarComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-item-meta-avatar",
      exportAs: "nzListItemMetaAvatar",
      template: `
    <div class="ant-list-item-meta-avatar">
      @if (nzSrc) {
        <nz-avatar [nzSrc]="nzSrc" />
      } @else {
        <ng-content />
      }
    </div>
  `,
      changeDetection: ChangeDetectionStrategy.OnPush,
      imports: [NzAvatarModule]
    }]
  }], null, {
    nzSrc: [{
      type: Input
    }]
  });
})();
var NzListItemMetaComponent = class _NzListItemMetaComponent {
  elementRef;
  avatarStr = "";
  avatarTpl;
  set nzAvatar(value) {
    if (value instanceof TemplateRef) {
      this.avatarStr = "";
      this.avatarTpl = value;
    } else {
      this.avatarStr = value;
    }
  }
  nzTitle;
  nzDescription;
  descriptionComponent;
  titleComponent;
  constructor(elementRef) {
    this.elementRef = elementRef;
  }
  static \u0275fac = function NzListItemMetaComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemMetaComponent)(\u0275\u0275directiveInject(ElementRef));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemMetaComponent,
    selectors: [["nz-list-item-meta"], ["", "nz-list-item-meta", ""]],
    contentQueries: function NzListItemMetaComponent_ContentQueries(rf, ctx, dirIndex) {
      if (rf & 1) {
        \u0275\u0275contentQuery(dirIndex, NzListItemMetaDescriptionComponent, 5);
        \u0275\u0275contentQuery(dirIndex, NzListItemMetaTitleComponent, 5);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.descriptionComponent = _t.first);
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.titleComponent = _t.first);
      }
    },
    hostAttrs: [1, "ant-list-item-meta"],
    inputs: {
      nzAvatar: "nzAvatar",
      nzTitle: "nzTitle",
      nzDescription: "nzDescription"
    },
    exportAs: ["nzListItemMeta"],
    ngContentSelectors: _c22,
    decls: 4,
    vars: 3,
    consts: [[3, "nzSrc"], [1, "ant-list-item-meta-content"], [3, "ngTemplateOutlet"], [4, "nzStringTemplateOutlet"]],
    template: function NzListItemMetaComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef(_c13);
        \u0275\u0275template(0, NzListItemMetaComponent_Conditional_0_Template, 1, 1, "nz-list-item-meta-avatar", 0)(1, NzListItemMetaComponent_Conditional_1_Template, 2, 1, "nz-list-item-meta-avatar");
        \u0275\u0275projection(2);
        \u0275\u0275template(3, NzListItemMetaComponent_Conditional_3_Template, 5, 2, "div", 1);
      }
      if (rf & 2) {
        \u0275\u0275conditional(ctx.avatarStr ? 0 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(ctx.avatarTpl ? 1 : -1);
        \u0275\u0275advance(2);
        \u0275\u0275conditional(ctx.nzTitle || ctx.nzDescription || ctx.descriptionComponent || ctx.titleComponent ? 3 : -1);
      }
    },
    dependencies: [NzListItemMetaAvatarComponent, NgTemplateOutlet, NzListItemMetaTitleComponent, NzOutletModule, NzStringTemplateOutletDirective, NzListItemMetaDescriptionComponent],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemMetaComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-item-meta, [nz-list-item-meta]",
      exportAs: "nzListItemMeta",
      template: `
    <!--Old API Start-->
    @if (avatarStr) {
      <nz-list-item-meta-avatar [nzSrc]="avatarStr" />
    }

    @if (avatarTpl) {
      <nz-list-item-meta-avatar>
        <ng-container [ngTemplateOutlet]="avatarTpl" />
      </nz-list-item-meta-avatar>
    }

    <!--Old API End-->

    <ng-content select="nz-list-item-meta-avatar" />

    @if (nzTitle || nzDescription || descriptionComponent || titleComponent) {
      <div class="ant-list-item-meta-content">
        <!--Old API Start-->

        @if (nzTitle && !titleComponent) {
          <nz-list-item-meta-title>
            <ng-container *nzStringTemplateOutlet="nzTitle">{{ nzTitle }}</ng-container>
          </nz-list-item-meta-title>
        }

        @if (nzDescription && !descriptionComponent) {
          <nz-list-item-meta-description>
            <ng-container *nzStringTemplateOutlet="nzDescription">{{ nzDescription }}</ng-container>
          </nz-list-item-meta-description>
        }
        <!--Old API End-->

        <ng-content select="nz-list-item-meta-title" />
        <ng-content select="nz-list-item-meta-description" />
      </div>
    }
  `,
      preserveWhitespaces: false,
      changeDetection: ChangeDetectionStrategy.OnPush,
      encapsulation: ViewEncapsulation.None,
      host: {
        class: "ant-list-item-meta"
      },
      imports: [NzListItemMetaAvatarComponent, NgTemplateOutlet, NzListItemMetaTitleComponent, NzOutletModule, NzListItemMetaDescriptionComponent]
    }]
  }], () => [{
    type: ElementRef
  }], {
    nzAvatar: [{
      type: Input
    }],
    nzTitle: [{
      type: Input
    }],
    nzDescription: [{
      type: Input
    }],
    descriptionComponent: [{
      type: ContentChild,
      args: [NzListItemMetaDescriptionComponent]
    }],
    titleComponent: [{
      type: ContentChild,
      args: [NzListItemMetaTitleComponent]
    }]
  });
})();
var NzListItemExtraComponent = class _NzListItemExtraComponent {
  static \u0275fac = function NzListItemExtraComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemExtraComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemExtraComponent,
    selectors: [["nz-list-item-extra"], ["", "nz-list-item-extra", ""]],
    hostAttrs: [1, "ant-list-item-extra"],
    exportAs: ["nzListItemExtra"],
    ngContentSelectors: _c04,
    decls: 1,
    vars: 0,
    template: function NzListItemExtraComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275projection(0);
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemExtraComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-item-extra, [nz-list-item-extra]",
      exportAs: "nzListItemExtra",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<ng-content></ng-content>`,
      host: {
        class: "ant-list-item-extra"
      }
    }]
  }], null, null);
})();
var NzListItemActionComponent = class _NzListItemActionComponent {
  templateRef;
  static \u0275fac = function NzListItemActionComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemActionComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemActionComponent,
    selectors: [["nz-list-item-action"]],
    viewQuery: function NzListItemActionComponent_Query(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275viewQuery(TemplateRef, 7);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.templateRef = _t.first);
      }
    },
    exportAs: ["nzListItemAction"],
    ngContentSelectors: _c04,
    decls: 1,
    vars: 0,
    template: function NzListItemActionComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275template(0, NzListItemActionComponent_ng_template_0_Template, 1, 0, "ng-template");
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemActionComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-item-action",
      exportAs: "nzListItemAction",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<ng-template><ng-content></ng-content></ng-template>`
    }]
  }], null, {
    templateRef: [{
      type: ViewChild,
      args: [TemplateRef, {
        static: true
      }]
    }]
  });
})();
var NzListItemActionsComponent = class _NzListItemActionsComponent {
  nzActions = [];
  nzListItemActions;
  actions = [];
  inputActionChanges$ = new Subject();
  contentChildrenChanges$ = defer(() => {
    if (this.nzListItemActions) {
      return of(null);
    }
    return this.initialized.pipe(mergeMap(() => this.nzListItemActions.changes.pipe(startWith(this.nzListItemActions))));
  });
  initialized = new Subject();
  constructor(cdr, destroy$) {
    merge(this.contentChildrenChanges$, this.inputActionChanges$).pipe(takeUntil(destroy$)).subscribe(() => {
      if (this.nzActions.length) {
        this.actions = this.nzActions;
      } else {
        this.actions = this.nzListItemActions.map((action) => action.templateRef);
      }
      cdr.detectChanges();
    });
  }
  ngOnChanges() {
    this.inputActionChanges$.next(null);
  }
  ngAfterContentInit() {
    this.initialized.next();
    this.initialized.complete();
  }
  static \u0275fac = function NzListItemActionsComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemActionsComponent)(\u0275\u0275directiveInject(ChangeDetectorRef), \u0275\u0275directiveInject(NzDestroyService));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemActionsComponent,
    selectors: [["ul", "nz-list-item-actions", ""]],
    contentQueries: function NzListItemActionsComponent_ContentQueries(rf, ctx, dirIndex) {
      if (rf & 1) {
        \u0275\u0275contentQuery(dirIndex, NzListItemActionComponent, 4);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.nzListItemActions = _t);
      }
    },
    hostAttrs: [1, "ant-list-item-action"],
    inputs: {
      nzActions: "nzActions"
    },
    exportAs: ["nzListItemActions"],
    features: [\u0275\u0275ProvidersFeature([NzDestroyService]), \u0275\u0275NgOnChangesFeature],
    attrs: _c32,
    decls: 2,
    vars: 0,
    consts: [[3, "ngTemplateOutlet"], [1, "ant-list-item-action-split"]],
    template: function NzListItemActionsComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275repeaterCreate(0, NzListItemActionsComponent_For_1_Template, 3, 2, "li", null, \u0275\u0275repeaterTrackByIdentity);
      }
      if (rf & 2) {
        \u0275\u0275repeater(ctx.actions);
      }
    },
    dependencies: [NgTemplateOutlet],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemActionsComponent, [{
    type: Component,
    args: [{
      selector: "ul[nz-list-item-actions]",
      exportAs: "nzListItemActions",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `
    @for (i of actions; track i) {
      <li>
        <ng-template [ngTemplateOutlet]="i" />
        @if (!$last) {
          <em class="ant-list-item-action-split"></em>
        }
      </li>
    }
  `,
      host: {
        class: "ant-list-item-action"
      },
      providers: [NzDestroyService],
      imports: [NgTemplateOutlet]
    }]
  }], () => [{
    type: ChangeDetectorRef
  }, {
    type: NzDestroyService
  }], {
    nzActions: [{
      type: Input
    }],
    nzListItemActions: [{
      type: ContentChildren,
      args: [NzListItemActionComponent]
    }]
  });
})();
var NzListEmptyComponent = class _NzListEmptyComponent {
  nzNoResult;
  static \u0275fac = function NzListEmptyComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListEmptyComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListEmptyComponent,
    selectors: [["nz-list-empty"]],
    hostAttrs: [1, "ant-list-empty-text"],
    inputs: {
      nzNoResult: "nzNoResult"
    },
    exportAs: ["nzListHeader"],
    decls: 1,
    vars: 2,
    consts: [[3, "nzComponentName", "specificContent"]],
    template: function NzListEmptyComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275element(0, "nz-embed-empty", 0);
      }
      if (rf & 2) {
        \u0275\u0275property("nzComponentName", "list")("specificContent", ctx.nzNoResult);
      }
    },
    dependencies: [NzEmptyModule, NzEmbedEmptyComponent],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListEmptyComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-empty",
      exportAs: "nzListHeader",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<nz-embed-empty [nzComponentName]="'list'" [specificContent]="nzNoResult"></nz-embed-empty>`,
      host: {
        class: "ant-list-empty-text"
      },
      imports: [NzEmptyModule]
    }]
  }], null, {
    nzNoResult: [{
      type: Input
    }]
  });
})();
var NzListHeaderComponent = class _NzListHeaderComponent {
  static \u0275fac = function NzListHeaderComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListHeaderComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListHeaderComponent,
    selectors: [["nz-list-header"]],
    hostAttrs: [1, "ant-list-header"],
    exportAs: ["nzListHeader"],
    ngContentSelectors: _c04,
    decls: 1,
    vars: 0,
    template: function NzListHeaderComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275projection(0);
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListHeaderComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-header",
      exportAs: "nzListHeader",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<ng-content></ng-content>`,
      host: {
        class: "ant-list-header"
      }
    }]
  }], null, null);
})();
var NzListFooterComponent = class _NzListFooterComponent {
  static \u0275fac = function NzListFooterComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListFooterComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListFooterComponent,
    selectors: [["nz-list-footer"]],
    hostAttrs: [1, "ant-list-footer"],
    exportAs: ["nzListFooter"],
    ngContentSelectors: _c04,
    decls: 1,
    vars: 0,
    template: function NzListFooterComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275projection(0);
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListFooterComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-footer",
      exportAs: "nzListFooter",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<ng-content></ng-content>`,
      host: {
        class: "ant-list-footer"
      }
    }]
  }], null, null);
})();
var NzListPaginationComponent = class _NzListPaginationComponent {
  static \u0275fac = function NzListPaginationComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListPaginationComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListPaginationComponent,
    selectors: [["nz-list-pagination"]],
    hostAttrs: [1, "ant-list-pagination"],
    exportAs: ["nzListPagination"],
    ngContentSelectors: _c04,
    decls: 1,
    vars: 0,
    template: function NzListPaginationComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef();
        \u0275\u0275projection(0);
      }
    },
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListPaginationComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-pagination",
      exportAs: "nzListPagination",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<ng-content></ng-content>`,
      host: {
        class: "ant-list-pagination"
      }
    }]
  }], null, null);
})();
var NzListLoadMoreDirective = class _NzListLoadMoreDirective {
  static \u0275fac = function NzListLoadMoreDirective_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListLoadMoreDirective)();
  };
  static \u0275dir = /* @__PURE__ */ \u0275\u0275defineDirective({
    type: _NzListLoadMoreDirective,
    selectors: [["nz-list-load-more"]],
    exportAs: ["nzListLoadMoreDirective"]
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListLoadMoreDirective, [{
    type: Directive,
    args: [{
      selector: "nz-list-load-more",
      exportAs: "nzListLoadMoreDirective"
    }]
  }], null, null);
})();
var NzListGridDirective = class _NzListGridDirective {
  static \u0275fac = function NzListGridDirective_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListGridDirective)();
  };
  static \u0275dir = /* @__PURE__ */ \u0275\u0275defineDirective({
    type: _NzListGridDirective,
    selectors: [["nz-list", "nzGrid", ""]],
    hostAttrs: [1, "ant-list-grid"]
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListGridDirective, [{
    type: Directive,
    args: [{
      selector: "nz-list[nzGrid]",
      host: {
        class: "ant-list-grid"
      }
    }]
  }], null, null);
})();
var NzListComponent = class _NzListComponent {
  directionality;
  nzDataSource;
  nzBordered = false;
  nzGrid = "";
  nzHeader;
  nzFooter;
  nzItemLayout = "horizontal";
  nzRenderItem = null;
  nzLoading = false;
  nzLoadMore = null;
  nzPagination;
  nzSize = "default";
  nzSplit = true;
  nzNoResult;
  nzListFooterComponent;
  nzListPaginationComponent;
  nzListLoadMoreDirective;
  hasSomethingAfterLastItem = false;
  dir = "ltr";
  itemLayoutNotifySource = new BehaviorSubject(this.nzItemLayout);
  destroy$ = new Subject();
  get itemLayoutNotify$() {
    return this.itemLayoutNotifySource.asObservable();
  }
  constructor(directionality) {
    this.directionality = directionality;
  }
  ngOnInit() {
    this.dir = this.directionality.value;
    this.directionality.change?.pipe(takeUntil(this.destroy$)).subscribe((direction) => {
      this.dir = direction;
    });
  }
  getSomethingAfterLastItem() {
    return !!(this.nzLoadMore || this.nzPagination || this.nzFooter || this.nzListFooterComponent || this.nzListPaginationComponent || this.nzListLoadMoreDirective);
  }
  ngOnChanges(changes) {
    if (changes.nzItemLayout) {
      this.itemLayoutNotifySource.next(this.nzItemLayout);
    }
  }
  ngOnDestroy() {
    this.itemLayoutNotifySource.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }
  ngAfterContentInit() {
    this.hasSomethingAfterLastItem = this.getSomethingAfterLastItem();
  }
  static \u0275fac = function NzListComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListComponent)(\u0275\u0275directiveInject(Directionality));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListComponent,
    selectors: [["nz-list"], ["", "nz-list", ""]],
    contentQueries: function NzListComponent_ContentQueries(rf, ctx, dirIndex) {
      if (rf & 1) {
        \u0275\u0275contentQuery(dirIndex, NzListFooterComponent, 5);
        \u0275\u0275contentQuery(dirIndex, NzListPaginationComponent, 5);
        \u0275\u0275contentQuery(dirIndex, NzListLoadMoreDirective, 5);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.nzListFooterComponent = _t.first);
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.nzListPaginationComponent = _t.first);
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.nzListLoadMoreDirective = _t.first);
      }
    },
    hostAttrs: [1, "ant-list"],
    hostVars: 16,
    hostBindings: function NzListComponent_HostBindings(rf, ctx) {
      if (rf & 2) {
        \u0275\u0275classProp("ant-list-rtl", ctx.dir === "rtl")("ant-list-vertical", ctx.nzItemLayout === "vertical")("ant-list-lg", ctx.nzSize === "large")("ant-list-sm", ctx.nzSize === "small")("ant-list-split", ctx.nzSplit)("ant-list-bordered", ctx.nzBordered)("ant-list-loading", ctx.nzLoading)("ant-list-something-after-last-item", ctx.hasSomethingAfterLastItem);
      }
    },
    inputs: {
      nzDataSource: "nzDataSource",
      nzBordered: [2, "nzBordered", "nzBordered", booleanAttribute],
      nzGrid: "nzGrid",
      nzHeader: "nzHeader",
      nzFooter: "nzFooter",
      nzItemLayout: "nzItemLayout",
      nzRenderItem: "nzRenderItem",
      nzLoading: [2, "nzLoading", "nzLoading", booleanAttribute],
      nzLoadMore: "nzLoadMore",
      nzPagination: "nzPagination",
      nzSize: "nzSize",
      nzSplit: [2, "nzSplit", "nzSplit", booleanAttribute],
      nzNoResult: "nzNoResult"
    },
    exportAs: ["nzList"],
    features: [\u0275\u0275NgOnChangesFeature],
    ngContentSelectors: _c5,
    decls: 14,
    vars: 8,
    consts: [[3, "nzSpinning"], [3, "min-height"], ["nz-row", "", 3, "nzGutter"], [1, "ant-list-items"], [3, "nzNoResult"], [3, "ngTemplateOutlet"], [4, "nzStringTemplateOutlet"], ["nz-col", "", 3, "nzSpan", "nzXs", "nzSm", "nzMd", "nzLg", "nzXl", "nzXXl"], [3, "ngTemplateOutlet", "ngTemplateOutletContext"]],
    template: function NzListComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef(_c42);
        \u0275\u0275template(0, NzListComponent_Conditional_0_Template, 2, 1, "nz-list-header");
        \u0275\u0275projection(1);
        \u0275\u0275elementStart(2, "nz-spin", 0);
        \u0275\u0275elementContainerStart(3);
        \u0275\u0275template(4, NzListComponent_Conditional_4_Template, 1, 2, "div", 1)(5, NzListComponent_Conditional_5_Template, 3, 1, "div", 2)(6, NzListComponent_Conditional_6_Template, 4, 0, "div", 3)(7, NzListComponent_Conditional_7_Template, 1, 1, "nz-list-empty", 4);
        \u0275\u0275elementContainerEnd();
        \u0275\u0275elementEnd();
        \u0275\u0275template(8, NzListComponent_Conditional_8_Template, 2, 1, "nz-list-footer");
        \u0275\u0275projection(9, 1);
        \u0275\u0275template(10, NzListComponent_ng_template_10_Template, 0, 0, "ng-template", 5);
        \u0275\u0275projection(11, 2);
        \u0275\u0275template(12, NzListComponent_Conditional_12_Template, 2, 1, "nz-list-pagination");
        \u0275\u0275projection(13, 3);
      }
      if (rf & 2) {
        \u0275\u0275conditional(ctx.nzHeader ? 0 : -1);
        \u0275\u0275advance(2);
        \u0275\u0275property("nzSpinning", ctx.nzLoading);
        \u0275\u0275advance(2);
        \u0275\u0275conditional(ctx.nzLoading && ctx.nzDataSource && ctx.nzDataSource.length === 0 ? 4 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(ctx.nzGrid && ctx.nzDataSource ? 5 : 6);
        \u0275\u0275advance(2);
        \u0275\u0275conditional(!ctx.nzLoading && ctx.nzDataSource && ctx.nzDataSource.length === 0 ? 7 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(ctx.nzFooter ? 8 : -1);
        \u0275\u0275advance(2);
        \u0275\u0275property("ngTemplateOutlet", ctx.nzLoadMore);
        \u0275\u0275advance(2);
        \u0275\u0275conditional(ctx.nzPagination ? 12 : -1);
      }
    },
    dependencies: [NgTemplateOutlet, NzListHeaderComponent, NzOutletModule, NzStringTemplateOutletDirective, NzSpinModule, NzSpinComponent, NzGridModule, NzColDirective, NzRowDirective, NzListEmptyComponent, NzListFooterComponent, NzListPaginationComponent],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListComponent, [{
    type: Component,
    args: [{
      selector: "nz-list, [nz-list]",
      exportAs: "nzList",
      template: `
    @if (nzHeader) {
      <nz-list-header>
        <ng-container *nzStringTemplateOutlet="nzHeader">{{ nzHeader }}</ng-container>
      </nz-list-header>
    }

    <ng-content select="nz-list-header" />

    <nz-spin [nzSpinning]="nzLoading">
      <ng-container>
        @if (nzLoading && nzDataSource && nzDataSource.length === 0) {
          <div [style.min-height.px]="53"></div>
        }
        @if (nzGrid && nzDataSource) {
          <div nz-row [nzGutter]="nzGrid.gutter || null">
            @for (item of nzDataSource; track item; let index = $index) {
              <div
                nz-col
                [nzSpan]="nzGrid.span || null"
                [nzXs]="nzGrid.xs || null"
                [nzSm]="nzGrid.sm || null"
                [nzMd]="nzGrid.md || null"
                [nzLg]="nzGrid.lg || null"
                [nzXl]="nzGrid.xl || null"
                [nzXXl]="nzGrid.xxl || null"
              >
                <ng-template
                  [ngTemplateOutlet]="nzRenderItem"
                  [ngTemplateOutletContext]="{ $implicit: item, index: index }"
                />
              </div>
            }
          </div>
        } @else {
          <div class="ant-list-items">
            @for (item of nzDataSource; track item; let index = $index) {
              <ng-container>
                <ng-template
                  [ngTemplateOutlet]="nzRenderItem"
                  [ngTemplateOutletContext]="{ $implicit: item, index: index }"
                />
              </ng-container>
            }
            <ng-content />
          </div>
        }

        @if (!nzLoading && nzDataSource && nzDataSource.length === 0) {
          <nz-list-empty [nzNoResult]="nzNoResult" />
        }
      </ng-container>
    </nz-spin>

    @if (nzFooter) {
      <nz-list-footer>
        <ng-container *nzStringTemplateOutlet="nzFooter">{{ nzFooter }}</ng-container>
      </nz-list-footer>
    }

    <ng-content select="nz-list-footer, [nz-list-footer]" />

    <ng-template [ngTemplateOutlet]="nzLoadMore"></ng-template>
    <ng-content select="nz-list-load-more, [nz-list-load-more]" />

    @if (nzPagination) {
      <nz-list-pagination>
        <ng-template [ngTemplateOutlet]="nzPagination" />
      </nz-list-pagination>
    }

    <ng-content select="nz-list-pagination, [nz-list-pagination]" />
  `,
      preserveWhitespaces: false,
      encapsulation: ViewEncapsulation.None,
      changeDetection: ChangeDetectionStrategy.OnPush,
      host: {
        class: "ant-list",
        "[class.ant-list-rtl]": `dir === 'rtl'`,
        "[class.ant-list-vertical]": 'nzItemLayout === "vertical"',
        "[class.ant-list-lg]": 'nzSize === "large"',
        "[class.ant-list-sm]": 'nzSize === "small"',
        "[class.ant-list-split]": "nzSplit",
        "[class.ant-list-bordered]": "nzBordered",
        "[class.ant-list-loading]": "nzLoading",
        "[class.ant-list-something-after-last-item]": "hasSomethingAfterLastItem"
      },
      imports: [NgTemplateOutlet, NzListHeaderComponent, NzOutletModule, NzSpinModule, NzGridModule, NzListEmptyComponent, NzListFooterComponent, NzListPaginationComponent]
    }]
  }], () => [{
    type: Directionality
  }], {
    nzDataSource: [{
      type: Input
    }],
    nzBordered: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzGrid: [{
      type: Input
    }],
    nzHeader: [{
      type: Input
    }],
    nzFooter: [{
      type: Input
    }],
    nzItemLayout: [{
      type: Input
    }],
    nzRenderItem: [{
      type: Input
    }],
    nzLoading: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzLoadMore: [{
      type: Input
    }],
    nzPagination: [{
      type: Input
    }],
    nzSize: [{
      type: Input
    }],
    nzSplit: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }],
    nzNoResult: [{
      type: Input
    }],
    nzListFooterComponent: [{
      type: ContentChild,
      args: [NzListFooterComponent]
    }],
    nzListPaginationComponent: [{
      type: ContentChild,
      args: [NzListPaginationComponent]
    }],
    nzListLoadMoreDirective: [{
      type: ContentChild,
      args: [NzListLoadMoreDirective]
    }]
  });
})();
var NzListItemComponent = class _NzListItemComponent {
  parentComp;
  cdr;
  nzActions = [];
  nzContent;
  nzExtra = null;
  nzNoFlex = false;
  listItemExtraDirective;
  itemLayout;
  itemLayout$;
  get isVerticalAndExtra() {
    return this.itemLayout === "vertical" && (!!this.listItemExtraDirective || !!this.nzExtra);
  }
  constructor(parentComp, cdr) {
    this.parentComp = parentComp;
    this.cdr = cdr;
  }
  ngAfterViewInit() {
    this.itemLayout$ = this.parentComp.itemLayoutNotify$.subscribe((val) => {
      this.itemLayout = val;
      this.cdr.detectChanges();
    });
  }
  ngOnDestroy() {
    if (this.itemLayout$) {
      this.itemLayout$.unsubscribe();
    }
  }
  static \u0275fac = function NzListItemComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListItemComponent)(\u0275\u0275directiveInject(NzListComponent), \u0275\u0275directiveInject(ChangeDetectorRef));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({
    type: _NzListItemComponent,
    selectors: [["nz-list-item"], ["", "nz-list-item", ""]],
    contentQueries: function NzListItemComponent_ContentQueries(rf, ctx, dirIndex) {
      if (rf & 1) {
        \u0275\u0275contentQuery(dirIndex, NzListItemExtraComponent, 5);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.listItemExtraDirective = _t.first);
      }
    },
    hostAttrs: [1, "ant-list-item"],
    hostVars: 2,
    hostBindings: function NzListItemComponent_HostBindings(rf, ctx) {
      if (rf & 2) {
        \u0275\u0275classProp("ant-list-item-no-flex", ctx.nzNoFlex);
      }
    },
    inputs: {
      nzActions: "nzActions",
      nzContent: "nzContent",
      nzExtra: "nzExtra",
      nzNoFlex: [2, "nzNoFlex", "nzNoFlex", booleanAttribute]
    },
    exportAs: ["nzListItem"],
    ngContentSelectors: _c8,
    decls: 8,
    vars: 1,
    consts: [["actionsTpl", ""], ["contentTpl", ""], ["extraTpl", ""], ["nz-list-item-actions", "", 3, "nzActions"], [4, "nzStringTemplateOutlet"], [1, "ant-list-item-main"], [3, "ngTemplateOutlet"]],
    template: function NzListItemComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275projectionDef(_c7);
        \u0275\u0275template(0, NzListItemComponent_ng_template_0_Template, 2, 1, "ng-template", null, 0, \u0275\u0275templateRefExtractor)(2, NzListItemComponent_ng_template_2_Template, 3, 1, "ng-template", null, 1, \u0275\u0275templateRefExtractor)(4, NzListItemComponent_ng_template_4_Template, 1, 0, "ng-template", null, 2, \u0275\u0275templateRefExtractor)(6, NzListItemComponent_Conditional_6_Template, 5, 4)(7, NzListItemComponent_Conditional_7_Template, 4, 4);
      }
      if (rf & 2) {
        \u0275\u0275advance(6);
        \u0275\u0275conditional(ctx.isVerticalAndExtra ? 6 : 7);
      }
    },
    dependencies: [NzListItemActionsComponent, NzOutletModule, NzStringTemplateOutletDirective, NgTemplateOutlet, NzListItemExtraComponent],
    encapsulation: 2,
    changeDetection: 0
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListItemComponent, [{
    type: Component,
    args: [{
      selector: "nz-list-item, [nz-list-item]",
      exportAs: "nzListItem",
      template: `
    <ng-template #actionsTpl>
      @if (nzActions && nzActions.length > 0) {
        <ul nz-list-item-actions [nzActions]="nzActions"></ul>
      }
      <ng-content select="nz-list-item-actions, [nz-list-item-actions]" />
    </ng-template>
    <ng-template #contentTpl>
      <ng-content select="nz-list-item-meta, [nz-list-item-meta]" />
      <ng-content />
      @if (nzContent) {
        <ng-container *nzStringTemplateOutlet="nzContent">{{ nzContent }}</ng-container>
      }
    </ng-template>
    <ng-template #extraTpl>
      <ng-content select="nz-list-item-extra, [nz-list-item-extra]" />
    </ng-template>

    @if (isVerticalAndExtra) {
      <div class="ant-list-item-main">
        <ng-template [ngTemplateOutlet]="contentTpl" />
        <ng-template [ngTemplateOutlet]="actionsTpl" />
      </div>
      @if (nzExtra) {
        <nz-list-item-extra>
          <ng-template [ngTemplateOutlet]="nzExtra" />
        </nz-list-item-extra>
      }
      <ng-template [ngTemplateOutlet]="extraTpl" />
    } @else {
      <ng-template [ngTemplateOutlet]="contentTpl" />
      <ng-template [ngTemplateOutlet]="nzExtra" />
      <ng-template [ngTemplateOutlet]="extraTpl" />
      <ng-template [ngTemplateOutlet]="actionsTpl" />
    }
  `,
      preserveWhitespaces: false,
      encapsulation: ViewEncapsulation.None,
      changeDetection: ChangeDetectionStrategy.OnPush,
      host: {
        class: "ant-list-item"
      },
      imports: [NzListItemActionsComponent, NzOutletModule, NgTemplateOutlet, NzListItemExtraComponent]
    }]
  }], () => [{
    type: NzListComponent
  }, {
    type: ChangeDetectorRef
  }], {
    nzActions: [{
      type: Input
    }],
    nzContent: [{
      type: Input
    }],
    nzExtra: [{
      type: Input
    }],
    nzNoFlex: [{
      type: Input,
      args: [{
        transform: booleanAttribute
      }]
    }, {
      type: HostBinding,
      args: ["class.ant-list-item-no-flex"]
    }],
    listItemExtraDirective: [{
      type: ContentChild,
      args: [NzListItemExtraComponent]
    }]
  });
})();
var DIRECTIVES = [NzListComponent, NzListHeaderComponent, NzListFooterComponent, NzListPaginationComponent, NzListEmptyComponent, NzListItemComponent, NzListItemMetaComponent, NzListItemMetaTitleComponent, NzListItemMetaDescriptionComponent, NzListItemMetaAvatarComponent, NzListItemActionsComponent, NzListItemActionComponent, NzListItemExtraComponent, NzListLoadMoreDirective, NzListGridDirective];
var NzListModule = class _NzListModule {
  static \u0275fac = function NzListModule_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _NzListModule)();
  };
  static \u0275mod = /* @__PURE__ */ \u0275\u0275defineNgModule({
    type: _NzListModule
  });
  static \u0275inj = /* @__PURE__ */ \u0275\u0275defineInjector({
    imports: [NzListComponent, NzListEmptyComponent, NzListItemComponent, NzListItemMetaComponent, NzListItemMetaAvatarComponent]
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NzListModule, [{
    type: NgModule,
    args: [{
      imports: [DIRECTIVES],
      exports: [DIRECTIVES]
    }]
  }], null, null);
})();

// src/app/pages/dashboard/dashboard.component.ts
function DashboardComponent_For_48_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "nz-skeleton", 20);
  }
  if (rf & 2) {
    \u0275\u0275property("nzAvatar", true)("nzActive", true)("nzTitle", false)("nzLoading", true);
  }
}
function DashboardComponent_For_48_Conditional_2_Template(rf, ctx) {
  if (rf & 1) {
    const _r1 = \u0275\u0275getCurrentView();
    \u0275\u0275elementContainerStart(0);
    \u0275\u0275elementStart(1, "nz-list-item-meta", 21)(2, "nz-list-item-meta-title")(3, "a", 22);
    \u0275\u0275text(4);
    \u0275\u0275elementEnd()()();
    \u0275\u0275text(5, " content ");
    \u0275\u0275elementStart(6, "ul", 23)(7, "nz-list-item-action")(8, "a", 24);
    \u0275\u0275listener("click", function DashboardComponent_For_48_Conditional_2_Template_a_click_8_listener() {
      \u0275\u0275restoreView(_r1);
      const item_r2 = \u0275\u0275nextContext().$implicit;
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.edit(item_r2));
    });
    \u0275\u0275text(9, "edit");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(10, "nz-list-item-action")(11, "a", 24);
    \u0275\u0275listener("click", function DashboardComponent_For_48_Conditional_2_Template_a_click_11_listener() {
      \u0275\u0275restoreView(_r1);
      const item_r2 = \u0275\u0275nextContext().$implicit;
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.edit(item_r2));
    });
    \u0275\u0275text(12, "more");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementContainerEnd();
  }
  if (rf & 2) {
    const item_r2 = \u0275\u0275nextContext().$implicit;
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(item_r2.name.last);
  }
}
function DashboardComponent_For_48_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "nz-list-item");
    \u0275\u0275template(1, DashboardComponent_For_48_Conditional_1_Template, 1, 4, "nz-skeleton", 20)(2, DashboardComponent_For_48_Conditional_2_Template, 13, 1, "ng-container");
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const item_r2 = ctx.$implicit;
    \u0275\u0275advance();
    \u0275\u0275conditional(item_r2.loading ? 1 : 2);
  }
}
function DashboardComponent_Conditional_50_Template(rf, ctx) {
  if (rf & 1) {
    const _r4 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "button", 25);
    \u0275\u0275listener("click", function DashboardComponent_Conditional_50_Template_button_click_0_listener() {
      \u0275\u0275restoreView(_r4);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.onLoadMore());
    });
    \u0275\u0275element(1, "nz-icon", 26);
    \u0275\u0275elementEnd();
  }
}
var count = 5;
var fakeDataUrl = "https://randomuser.me/api/?results=5&inc=name,gender,email,nat&noinfo";
var DashboardComponent = class _DashboardComponent {
  http;
  msg;
  uiConfigService;
  authenticationService;
  router;
  initLoading = true;
  // bug
  loadingMore = false;
  data = [];
  list = [];
  constructor(http, msg, uiConfigService, authenticationService, router) {
    this.http = http;
    this.msg = msg;
    this.uiConfigService = uiConfigService;
    this.authenticationService = authenticationService;
    this.router = router;
  }
  ngOnInit() {
    if (!this.authenticationService.isUserLoggedIn()) {
      this.router.navigate(["/login"]);
    }
    const apiUrl = this.uiConfigService.getApiUrl();
    console.log("API URL:", apiUrl);
    this.getData((res) => {
      this.data = res.results;
      this.list = res.results;
      this.initLoading = false;
    });
  }
  alertsTodayData = {
    labels: ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00"],
    datasets: [{ label: "Alerts", data: [5, 2, 1, 7, 3, 10, 7, 8, 9], backgroundColor: "#F06D0F" }]
  };
  alertStatusData = {
    labels: ["Resolved", "Unresolved"],
    datasets: [{ data: [34, 18], backgroundColor: ["#F06D0F", "#F00F21"], borderWidth: 1, borderColor: "#F38A3F" }]
  };
  monthlyAlertsData = {
    labels: ["Apr 1", "Apr 2", "Apr 3", "Apr 4", "Apr 5", "Apr 6", "Apr 7", "Apr 8", "Apr 9", "Apr 10", "Apr 11", "Apr 12", "Apr 13", "Apr 14", "Apr 15", "Apr 16", "Apr 17", "Apr 18", "Apr 19", "Apr 20", "Apr 21", "Apr 22", "Apr 23", "Apr 24", "Apr 25", "Apr 26", "Apr 27", "Apr 28", "Apr 29", "Apr 30", "..."],
    datasets: [
      { label: "Critical", data: [10, 5, 2, 3, 8, 9, 3, 1, 24, 0, 5, 9, 16, 2, 1, 15, 21, 5, 6, 9, 5, 1, 2, 8, 5, 3, 1, 5, 3, 4], backgroundColor: "#F00F21" },
      { label: "Warning", data: [20, 10, 7, 2, 5, 4, 12, 2, 5, 1, 8, 3, 4, 4, 8, 3, 3, 8, 18, 7, 14, 5, 2, 18, 3, 1, 17, 16, 8, 48], backgroundColor: "#F06D0F" }
    ]
  };
  mttrLineData = {
    labels: ["Apr 28", "Apr 29", "Apr 30"],
    datasets: [
      {
        label: "MTTR (hours)",
        data: [2.5, 2.1, 1.9],
        borderColor: "#F06D0F",
        tension: 0.4
      }
    ]
  };
  userAlertLoadData = {
    labels: ["Alice", "Bob", "Carol"],
    datasets: [{ label: "Assigned Alerts", data: [12, 9, 7], backgroundColor: "#59C7FF" }]
  };
  barChartOptions = { responsive: true, maintainAspectRatio: false };
  stackedBarOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: { x: { stacked: true }, y: { stacked: true } }
  };
  lineChartOptions = { responsive: true, maintainAspectRatio: false };
  doughnutOptions = {
    responsive: true,
    maintainAspectRatio: false
  };
  getData(callback) {
    this.http.get(fakeDataUrl).pipe(catchError(() => of({ results: [] }))).subscribe((res) => callback(res));
  }
  onLoadMore() {
    this.loadingMore = true;
    this.list = this.data.concat([...Array(count)].fill({}).map(() => ({ loading: true, name: {} })));
    this.http.get(fakeDataUrl).pipe(catchError(() => of({ results: [] }))).subscribe((res) => {
      this.data = this.data.concat(res.results);
      this.list = [...this.data];
      this.loadingMore = false;
    });
  }
  edit(item) {
    this.msg.success(item.email);
  }
  static \u0275fac = function DashboardComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _DashboardComponent)(\u0275\u0275directiveInject(HttpClient), \u0275\u0275directiveInject(NzMessageService), \u0275\u0275directiveInject(UiConfigService), \u0275\u0275directiveInject(AuthenticationService), \u0275\u0275directiveInject(Router));
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _DashboardComponent, selectors: [["app-dashboard"]], decls: 51, vars: 37, consts: [[1, "app-layout"], ["nzCollapsible", "", "nzCollapsed", "true", 1, "menu-sidebar", 3, "nzTrigger"], [1, "inner-content", "p-4"], [1, "mb-6", 3, "nzGutter"], [3, "nzSpan"], ["nzTitle", "Alerts Today", 1, "tails-primary-color", 3, "nzBordered"], ["nzTitle", "Resolved", 1, "tails-primary-color", 3, "nzBordered"], ["nzTitle", "Not Resolved", 1, "tails-primary-color", 3, "nzBordered"], ["nzTitle", "Avg. MTTR", 1, "tails-primary-color", 3, "nzBordered"], ["nzTitle", "Tail Alerts (Hourly)", 3, "nzBordered"], [2, "height", "200px"], ["baseChart", "", 3, "data", "options", "type"], ["nzTitle", "Tail Resolution Status", 3, "nzBordered"], [3, "nzGutter"], ["nzTitle", "Monthly Alerts (Stacked)", 3, "nzBordered"], ["nzTitle", "Mean Time To Resolution", 3, "nzBordered"], ["nzTitle", "Active Tails", 3, "nzBordered"], [1, "demo-loadmore-list", 3, "nzLoading"], ["nz-list-load-more", "", 1, "loadmore"], ["nz-button", "", "nzType", "primary", 2, "float", "right", "background-color", "#F06D0F", "padding", "5px", "border-radius", "3px", "border", "none"], [3, "nzAvatar", "nzActive", "nzTitle", "nzLoading"], ["nzAvatar", "n1netails_icon_transparent.png", "nzDescription", "Ant Design, a design language for background applications, is refined by Ant UED Team"], ["href", "https://ng.ant.design"], ["nz-list-item-actions", ""], [3, "click"], ["nz-button", "", "nzType", "primary", 2, "float", "right", "background-color", "#F06D0F", "padding", "5px", "border-radius", "3px", "border", "none", 3, "click"], ["nzType", "plus", "nzTheme", "outline"]], template: function DashboardComponent_Template(rf, ctx) {
    if (rf & 1) {
      \u0275\u0275elementStart(0, "nz-layout", 0)(1, "nz-sider", 1);
      \u0275\u0275element(2, "app-sidenav");
      \u0275\u0275elementEnd();
      \u0275\u0275elementStart(3, "nz-layout")(4, "nz-header");
      \u0275\u0275element(5, "app-header");
      \u0275\u0275elementEnd();
      \u0275\u0275elementStart(6, "nz-content")(7, "div", 2)(8, "nz-row", 3)(9, "nz-col", 4)(10, "nz-card", 5)(11, "b");
      \u0275\u0275text(12, "52");
      \u0275\u0275elementEnd()()();
      \u0275\u0275elementStart(13, "nz-col", 4)(14, "nz-card", 6)(15, "b");
      \u0275\u0275text(16, "34");
      \u0275\u0275elementEnd()()();
      \u0275\u0275elementStart(17, "nz-col", 4)(18, "nz-card", 7)(19, "b");
      \u0275\u0275text(20, "18");
      \u0275\u0275elementEnd()()();
      \u0275\u0275elementStart(21, "nz-col", 4)(22, "nz-card", 8)(23, "b");
      \u0275\u0275text(24, "2h 15m");
      \u0275\u0275elementEnd()()()();
      \u0275\u0275elementStart(25, "nz-row", 3)(26, "nz-col", 4)(27, "nz-card", 9)(28, "div", 10);
      \u0275\u0275element(29, "canvas", 11);
      \u0275\u0275elementEnd()()();
      \u0275\u0275elementStart(30, "nz-col", 4)(31, "nz-card", 12)(32, "div", 10);
      \u0275\u0275element(33, "canvas", 11);
      \u0275\u0275elementEnd()()()();
      \u0275\u0275elementStart(34, "nz-row", 13)(35, "nz-col", 4)(36, "nz-card", 14)(37, "div", 10);
      \u0275\u0275element(38, "canvas", 11);
      \u0275\u0275elementEnd()()();
      \u0275\u0275elementStart(39, "nz-col", 4)(40, "nz-card", 15)(41, "div", 10);
      \u0275\u0275element(42, "canvas", 11);
      \u0275\u0275elementEnd()()()();
      \u0275\u0275elementStart(43, "nz-row", 13)(44, "nz-col", 4)(45, "nz-card", 16)(46, "nz-list", 17);
      \u0275\u0275repeaterCreate(47, DashboardComponent_For_48_Template, 3, 1, "nz-list-item", null, \u0275\u0275repeaterTrackByIdentity);
      \u0275\u0275elementStart(49, "div", 18);
      \u0275\u0275template(50, DashboardComponent_Conditional_50_Template, 2, 0, "button", 19);
      \u0275\u0275elementEnd()()()()()()()()();
    }
    if (rf & 2) {
      \u0275\u0275advance();
      \u0275\u0275property("nzTrigger", null);
      \u0275\u0275advance(7);
      \u0275\u0275property("nzGutter", 8);
      \u0275\u0275advance();
      \u0275\u0275property("nzSpan", 6);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(3);
      \u0275\u0275property("nzSpan", 6);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(3);
      \u0275\u0275property("nzSpan", 6);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(3);
      \u0275\u0275property("nzSpan", 6);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(3);
      \u0275\u0275property("nzGutter", 8);
      \u0275\u0275advance();
      \u0275\u0275property("nzSpan", 16);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(2);
      \u0275\u0275property("data", ctx.alertsTodayData)("options", ctx.barChartOptions)("type", "bar");
      \u0275\u0275advance();
      \u0275\u0275property("nzSpan", 8);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(2);
      \u0275\u0275property("data", ctx.alertStatusData)("options", ctx.doughnutOptions)("type", "doughnut");
      \u0275\u0275advance();
      \u0275\u0275property("nzGutter", 8);
      \u0275\u0275advance();
      \u0275\u0275property("nzSpan", 16);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(2);
      \u0275\u0275property("data", ctx.monthlyAlertsData)("options", ctx.stackedBarOptions)("type", "bar");
      \u0275\u0275advance();
      \u0275\u0275property("nzSpan", 8);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance(2);
      \u0275\u0275property("data", ctx.mttrLineData)("options", ctx.lineChartOptions)("type", "line");
      \u0275\u0275advance();
      \u0275\u0275property("nzGutter", 8);
      \u0275\u0275advance();
      \u0275\u0275property("nzSpan", 24);
      \u0275\u0275advance();
      \u0275\u0275property("nzBordered", false);
      \u0275\u0275advance();
      \u0275\u0275property("nzLoading", ctx.initLoading);
      \u0275\u0275advance();
      \u0275\u0275repeater(ctx.list);
      \u0275\u0275advance(3);
      \u0275\u0275conditional(!ctx.loadingMore ? 50 : -1);
    }
  }, dependencies: [NzIconModule, NzIconDirective, NzLayoutModule, NzLayoutComponent, NzHeaderComponent, NzContentComponent, NzSiderComponent, NzCardModule, NzCardComponent, NzGridModule, NzColDirective, NzRowDirective, NzAvatarModule, NzListModule, NzListComponent, NzListItemComponent, NzListItemMetaComponent, NzListItemMetaTitleComponent, NzListItemActionsComponent, NzListItemActionComponent, NzSkeletonModule, NzSkeletonComponent, BaseChartDirective, HeaderComponent, SidenavComponent], styles: ["\n\n[_nghost-%COMP%] {\n  display: flex;\n  text-rendering: optimizeLegibility;\n  -webkit-font-smoothing: antialiased;\n  -moz-osx-font-smoothing: grayscale;\n}\n.app-layout[_ngcontent-%COMP%] {\n  height: 100vh;\n}\n.menu-sidebar[_ngcontent-%COMP%] {\n  position: relative;\n  z-index: 10;\n  min-height: 100vh;\n  border-right: 1px solid #F06D0F;\n}\nnz-header[_ngcontent-%COMP%] {\n  padding: 0;\n  width: 100%;\n  z-index: 2;\n}\nnz-content[_ngcontent-%COMP%] {\n  margin: 0px;\n  overflow-y: auto;\n  overflow-x: hidden;\n}\n.inner-content[_ngcontent-%COMP%] {\n  padding-right: 8px;\n  padding-left: 8px;\n}\nnz-row[_ngcontent-%COMP%] {\n  margin: 8px;\n}\n/*# sourceMappingURL=dashboard.component.css.map */"] });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(DashboardComponent, [{
    type: Component,
    args: [{ selector: "app-dashboard", imports: [NzIconModule, NzLayoutModule, NzCardModule, NzGridModule, NzAvatarModule, NzListModule, NzSkeletonModule, BaseChartDirective, HeaderComponent, SidenavComponent], template: `<nz-layout class="app-layout">\r
  <nz-sider class="menu-sidebar" nzCollapsible nzCollapsed="true" [nzTrigger]="null">\r
    <app-sidenav></app-sidenav>\r
  </nz-sider>\r
\r
  <nz-layout>\r
    <nz-header>\r
      <app-header></app-header>\r
    </nz-header>\r
\r
    <nz-content>\r
      <div class="inner-content p-4">\r
        <!-- KPI Cards -->\r
        <nz-row class="kpi-cards" [nzGutter]="8" class="mb-6">\r
          <nz-col [nzSpan]="6">\r
            <nz-card class="tails-primary-color" nzTitle="Alerts Today" [nzBordered]="false"><b>52</b></nz-card>\r
          </nz-col>\r
          <nz-col [nzSpan]="6">\r
            <nz-card class="tails-primary-color" nzTitle="Resolved" [nzBordered]="false"><b>34</b></nz-card>\r
          </nz-col>\r
          <nz-col [nzSpan]="6">\r
            <nz-card class="tails-primary-color" nzTitle="Not Resolved" [nzBordered]="false"><b>18</b></nz-card>\r
          </nz-col>\r
          <nz-col [nzSpan]="6">\r
            <nz-card class="tails-primary-color" nzTitle="Avg. MTTR" [nzBordered]="false"><b>2h 15m</b></nz-card>\r
          </nz-col>\r
        </nz-row>\r
\r
        <!-- Charts Row 1 -->\r
        <nz-row [nzGutter]="8" class="mb-6">\r
          <nz-col [nzSpan]="16">\r
            <nz-card nzTitle="Tail Alerts (Hourly)" [nzBordered]="false">\r
              <div style="height: 200px">\r
                <canvas baseChart [data]="alertsTodayData" [options]="barChartOptions" [type]="'bar'">\r
                </canvas>\r
              </div>\r
            </nz-card>\r
          </nz-col>\r
          <nz-col [nzSpan]="8">\r
            <nz-card nzTitle="Tail Resolution Status" [nzBordered]="false">\r
              <div style="height: 200px">\r
                <canvas baseChart [data]="alertStatusData" [options]="doughnutOptions" [type]="'doughnut'">\r
                </canvas>\r
              </div>\r
            </nz-card>\r
          </nz-col>\r
        </nz-row>\r
\r
        <!-- Charts Row 2 -->\r
        <nz-row [nzGutter]="8">\r
          <nz-col [nzSpan]="16">\r
            <nz-card nzTitle="Monthly Alerts (Stacked)" [nzBordered]="false">\r
              <div style="height: 200px">\r
                <canvas baseChart [data]="monthlyAlertsData" [options]="stackedBarOptions" [type]="'bar'">\r
                </canvas>\r
              </div>\r
\r
            </nz-card>\r
          </nz-col>\r
          <nz-col [nzSpan]="8">\r
            <nz-card nzTitle="Mean Time To Resolution" [nzBordered]="false">\r
              <div style="height: 200px">\r
                <canvas baseChart [data]="mttrLineData" [options]="lineChartOptions" [type]="'line'">\r
                </canvas>\r
              </div>\r
            </nz-card>\r
          </nz-col>\r
        </nz-row>\r
\r
        <nz-row [nzGutter]="8">\r
          <nz-col [nzSpan]="24">\r
            <nz-card nzTitle="Active Tails" [nzBordered]="false">\r
              <nz-list class="demo-loadmore-list" [nzLoading]="initLoading">\r
                @for (item of list; track item) {\r
                <nz-list-item>\r
                  @if (item.loading) {\r
                  <nz-skeleton [nzAvatar]="true" [nzActive]="true" [nzTitle]="false" [nzLoading]="true" />\r
                  } @else {\r
                  <ng-container>\r
                    <nz-list-item-meta nzAvatar="n1netails_icon_transparent.png"\r
                      nzDescription="Ant Design, a design language for background applications, is refined by Ant UED Team">\r
                      <nz-list-item-meta-title>\r
                        <a href="https://ng.ant.design">{{ item.name.last }}</a>\r
                      </nz-list-item-meta-title>\r
                    </nz-list-item-meta>\r
                    content\r
                    <ul nz-list-item-actions>\r
                      <nz-list-item-action><a (click)="edit(item)">edit</a></nz-list-item-action>\r
                      <nz-list-item-action><a (click)="edit(item)">more</a></nz-list-item-action>\r
                    </ul>\r
                  </ng-container>\r
                  }\r
                </nz-list-item>\r
                }\r
                <div class="loadmore" nz-list-load-more>\r
                  @if (!loadingMore) {\r
                  <button\r
                    style="float: right; background-color: #F06D0F; padding: 5px; border-radius: 3px; border: none;"\r
                    nz-button nzType="primary" (click)="onLoadMore()">\r
                    <nz-icon nzType="plus" nzTheme="outline" />\r
                  </button>\r
                  }\r
                </div>\r
              </nz-list>\r
            </nz-card>\r
          </nz-col>\r
        </nz-row>\r
      </div>\r
    </nz-content>\r
\r
  </nz-layout>\r
</nz-layout>\r
`, styles: ["/* src/app/pages/dashboard/dashboard.component.less */\n:host {\n  display: flex;\n  text-rendering: optimizeLegibility;\n  -webkit-font-smoothing: antialiased;\n  -moz-osx-font-smoothing: grayscale;\n}\n.app-layout {\n  height: 100vh;\n}\n.menu-sidebar {\n  position: relative;\n  z-index: 10;\n  min-height: 100vh;\n  border-right: 1px solid #F06D0F;\n}\nnz-header {\n  padding: 0;\n  width: 100%;\n  z-index: 2;\n}\nnz-content {\n  margin: 0px;\n  overflow-y: auto;\n  overflow-x: hidden;\n}\n.inner-content {\n  padding-right: 8px;\n  padding-left: 8px;\n}\nnz-row {\n  margin: 8px;\n}\n/*# sourceMappingURL=dashboard.component.css.map */\n"] }]
  }], () => [{ type: HttpClient }, { type: NzMessageService }, { type: UiConfigService }, { type: AuthenticationService }, { type: Router }], null);
})();
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(DashboardComponent, { className: "DashboardComponent", filePath: "src/app/pages/dashboard/dashboard.component.ts", lineNumber: 29 });
})();

// src/app/pages/dashboard/dashboard.routes.ts
var DASHBOARD_ROUTES = [
  { path: "", component: DashboardComponent }
];
export {
  DASHBOARD_ROUTES
};
//# sourceMappingURL=chunk-T7GATMYK.js.map
