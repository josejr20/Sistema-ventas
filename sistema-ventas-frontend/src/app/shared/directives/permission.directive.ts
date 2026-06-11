import { Directive, Input, TemplateRef, ViewContainerRef, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Directive({ selector: '[appPermission]' })
export class PermissionDirective {
  @Input('appPermission') allowedRoles: string[] = [];
  @Input('appPermissionMode') mode: 'all' | 'any' = 'any';

  private authService = inject(AuthService);

  constructor(private templateRef: TemplateRef<any>, private viewContainer: ViewContainerRef) {}

  ngOnInit(): void {
    const userRoles = this.authService.currentUser()?.roles ?? [];
    const hasAccess = this.mode === 'all'
      ? this.allowedRoles.every((role) => userRoles.includes(role))
      : this.allowedRoles.some((role) => userRoles.includes(role));

    if (hasAccess) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
