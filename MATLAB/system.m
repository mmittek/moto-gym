[data] = loadCSVAndPreprocess('../Data/2016-09-26 11_00_07_477.csv');

[t data dt] = sampleUniformly( data(:,1), data(:,2:end) );
t = t(10:end);
data = data(10:end,:);


data = [t data];

accel = data(:,2:4);
angles = data(:,14:16);

plotR = 1;
plotC = 4;

load('lowpass.mat')
accelDc = filter(LowPass, accel);
accelNoDc = accel - accelDc;


grav = data(:,8:10);

% Extraction of the 2D coordinates
[x,y,z] = gpsToXYZ( data(:,27), data(:,28), data(:,29) );
x = x-x(1);
y = y-y(1);
z = z-z(1);

rotMats = data(:,17:17+9-1);
step = 1;

boxwh= [0.5 2];
Box = [ -boxwh(1),-boxwh(2); -boxwh(1),boxwh(2); boxwh(1),boxwh(2); boxwh(1),-boxwh(2); -boxwh(1),-boxwh(2) ]./50;

figure(1)
clf;
for i=size(x,1)-10000:step:size(x,1)
    accelMag = norm( accel(i,:) )/10;


    accAngle = accelDc(i,[1,3]);
    
    angle = mean( angles( max([i-10,1]):i ,1) );
    
    R = [ cos(angle) -sin(angle)
           sin(angle) cos(angle) ];
    
    RotBox =  Box*R;
    ts = data(i,1);
%     videoReader.CurrentTime = ts/1000;
%     vidFrame = imresize(readFrame(videoReader), [1080,1920]./8);
    
    figure(1)
%    subplot(1,plotC,1)
    plot(x(1:i),y(1:i), 'r-');
    
    
    hold on;
    
    
    text( x(i), y(i), sprintf('Speed: %.1f\nAccelMag: %.1f', data(i,end-4)*3.6 , accelMag) );
%     plot( [ x(i), x(i)+R(1,1)./20 ],[ y(i), y(i)+R(1,2)./20],'r-x' );
%     plot( [ x(i), x(i)+R(2,1)./20 ],[ y(i), y(i)+R(2,2)./20],'g-x' );
%     plot( [ x(i), x(i)+R(3,1)./20 ],[ y(i), y(i)+R(3,2)./20],'b-x' );

%        plot([x(i) x(i)+linAccel(i,2)],[y(i) y(i)+linAccel(i,3)],'r-x');
%        plot3( [x(i), x(i)+grav(i,1)],  [y(i), y(i)+grav(i,2)], [0,grav(i,3)], 'b-x' );

%        plot( [x(i), x(i)+sin(angles(i,1)) ],  [y(i), y(i)+ cos(angles(i,1)) ],  'b-x' );
        
        
        
        plot( x(i)+RotBox(:,1), y(i)+RotBox(:,2), 'm-.' );
        
        plot( x(i)+accAngle(1), y(i)+accAngle(2), 'b-.' );

    hold off;
    grid on;
    axis equal;
    axis([ min(x),max(x),min(y),max(y)]);
    xlabel('X [miles]');
    ylabel('Y [miles]');
    title('GPS data');
    
    figure(2)
     subplot(1,plotC,2)
     plot( [0, 0 ],  [0, accelDc(i,1)  ],  'b-x' );
    grid on;
    axis([-10,10,-10,10]);
    title('X');
     subplot(1,plotC,3)
     
     plot( [0, 0 ],  [0, accelDc(i,2)  ],  'b-x' );
    grid on;
    axis([-10,10,-10,10]);
    title('Y');

         subplot(1,plotC,4)
     
     plot( [0, 0 ],  [0, accelDc(i,3)  ],  'b-x' );
    grid on;
    axis([-10,10,-10,10]);
    title('Z');


%     imshow(vidFrame);    
    
    drawnow;
end
