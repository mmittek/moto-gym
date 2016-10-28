function [absOrient] = getAbsoluteOrientation(grav, mag)

    box4d =  makeBox(1,0.5,4);
    
    
    grav = reshape(grav, [3,1])./ norm(grav);
    mag = reshape(mag, [3,1])./ norm(mag);
    fwd = [ 0,0,-1 ]';
    side = cross( grav,fwd );
    up = cross(side,grav);
%    side = cross( grav,mag );
%    up = cross( side,grav );
    
    up = up ./ norm(up);
    up(2 ) = abs(up(2));
    
%     R = [ side, mag, grav ];
%     R4D = R3DtoR4D(R);
%     %boxr = (R4D*box4d')';
%     boxr = (R4D*box4d')'
%     figure(5)
%     plot(0,0);
%     plotVec3D( up(1), up(3), up(2) );
%     hold on;
%     plot3( boxr(:,1), boxr(:,3), boxr(:,2) );
%     hold off;
%     axis equal;
%     axis([-1,1,-1,1,-1,1]);
%     grid on;
%     view(2)
%     drawnow;
    
    
    absOrient = [ grav ,mag, side ];
    
end