import {
  Component,
  setClassMetadata,
  ɵsetClassDebugInfo,
  ɵɵdefineComponent,
  ɵɵelementEnd,
  ɵɵelementStart,
  ɵɵtext
} from "./chunk-446R5MAN.js";

// src/app/pages/welcome/welcome.component.ts
var WelcomeComponent = class _WelcomeComponent {
  constructor() {
  }
  static \u0275fac = function WelcomeComponent_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _WelcomeComponent)();
  };
  static \u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _WelcomeComponent, selectors: [["app-welcome"]], decls: 2, vars: 0, template: function WelcomeComponent_Template(rf, ctx) {
    if (rf & 1) {
      \u0275\u0275elementStart(0, "p");
      \u0275\u0275text(1, "welcome works!");
      \u0275\u0275elementEnd();
    }
  }, encapsulation: 2 });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(WelcomeComponent, [{
    type: Component,
    args: [{ selector: "app-welcome", template: "<p>welcome works!</p>\r\n" }]
  }], () => [], null);
})();
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(WelcomeComponent, { className: "WelcomeComponent", filePath: "src/app/pages/welcome/welcome.component.ts", lineNumber: 8 });
})();

// src/app/pages/welcome/welcome.routes.ts
var WELCOME_ROUTES = [
  { path: "", component: WelcomeComponent }
];
export {
  WELCOME_ROUTES
};
//# sourceMappingURL=chunk-FBIRKIIT.js.map
