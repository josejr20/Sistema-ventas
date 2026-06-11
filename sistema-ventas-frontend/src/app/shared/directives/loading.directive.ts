import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({ selector: '[appLoading]' })
export class LoadingDirective {
  private isLoading = false;

  constructor(private el: ElementRef) {}

  @Input('appLoading') set loading(value: boolean) {
    this.isLoading = value;
    this.toggleLoading();
  }

  @HostListener('mouseenter') onMouseEnter(): void {
    if (!this.isLoading) {
      this.el.nativeElement.style.cursor = 'pointer';
    }
  }

  private toggleLoading(): void {
    if (this.isLoading) {
      this.el.nativeElement.setAttribute('disabled', 'true');
      this.el.nativeElement.style.opacity = '0.6';
      this.el.nativeElement.style.pointerEvents = 'none';
    } else {
      this.el.nativeElement.removeAttribute('disabled');
      this.el.nativeElement.style.opacity = '1';
      this.el.nativeElement.style.pointerEvents = 'auto';
    }
  }
}
