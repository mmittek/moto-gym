clear variables;
init;
load('20161026_two_sources_prep.mat');

% Getting time step and sampling frequency
dt = (t(2)-t(1))/1000000;
Fs = 1000/dt;

data = data1;
idx = find( data(:,GPSACC) < 12 );
data = data(idx,:);

% First time to see which idx we want
[x,y,z,mx,my,mz,idx99] =  gpsToXYZ(data(:,LAT), data(:,LON), data(:,ALT));
dt = [0; data(2:end,1)-data(1:end-1,1)]./1000;

load('lowpass.mat');
accf = filter(LowPass, data(:, [ACCX, ACCY, ACCZ]));



clear variables;
init;
load('20161026_two_sources_prep.mat');

data = data1;
ref = [0,0,-1]; % vector pointing into the devices screen

% lfp
lpfn = 100;
grav = filter( [1],ones(1,lpfn)./lpfn, data(:,[ACCX,ACCY,ACCZ]) );
processOrientation(grav,ref);



grav = data(:, [GRAVX, GRAVY,GRAVZ]);
processOrientation(grav,ref);



acceleration = data(:,[ACCX,ACCY,ACCZ])-accf;

% Forward and sideways
accFwd = sqrt(sum(acceleration(:,[2,3]).^2,2));
accSide = sqrt(sum(acceleration(:,[2,1]).^2,2));


w = 1;
h = 0.25;
box2d = [ -w,-h; -w,h; w,h; w,-h; -w,-h ];
box3d = [box2d zeros(size(box2d,1),1)];
box4d = [ box3d zeros(size(box3d,1),1) ];
for i=500:10:size(data,1)
    figure(1)
    subplot(2,2,1);
    plot( x,y );
    hold on;
    plot( x(1:i), y(1:i), 'r-x' );
%    plot( x(1:i)+data(i,LACCZ).*100, y(1:i)+data(i,LACCX).*100, 'm-o' );
    hold off
    grid on;
    axis equal;
    drawnow;
    
    subplot(2,2,2);
    
    grav = data(i,[GRAVX, GRAVY,GRAVZ]);
    grav = grav./norm(grav);
    ref = [0,0,-1]; % vector pointing forward

    side = cross(ref,grav);
    up = cross(side,grav);

    R = [ grav; side; up ]';
    cmap = colormap(hsv(3));
        plot3( [0 R(1,1)], [0 R(3,1)],[0 R(2,1)], '-x', 'Color', cmap(1,:));
    hold on;
    for j=2:3
        plot3( [0 R(1,j)], [0 R(3,j)],[0 R(2,j)], '-x', 'Color', cmap(j,:));
    end
    
    R4 = zeros(4,4);
    R4(4,4) =1;
    R4(1:3,1:3) = R;
    boxr = box4d*R4;
    boxr = (R4*box4d')';
    plot3( boxr(:,1), boxr(:,3), boxr(:,2), 'b--x' );
    
    hold off;
    axis equal;
    axis([-1,1,-1,1,-1,1]);
    grid on;
    view(2)
    title('Device orientation');
    drawnow;
    
    subplot(2,2,3)
    plot( boxr(:,1), boxr(:,2), 'b-o' );
    axis equal;
    title('Front view');
        axis([-1,1,-1,1]);


    subplot(2,2,4)
    plot( boxr(:,1), boxr(:,3), 'b-o' );
        axis([-1,1,-1,1]);
    axis equal;
    title('Top view');
    
end


